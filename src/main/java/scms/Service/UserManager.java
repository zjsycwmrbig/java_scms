package scms.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import scms.Dao.DataItem;
import scms.Dao.UserRBTree;
import scms.Interceptor.BridgeData;
import scms.Interceptor.FileManager;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.GetJson.GetUserData;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.OnlineData;
import scms.domain.ServerJson.UserFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/30 9:43
 * @function 静态类管理用户信息 用户权限的服务类
 */
public class UserManager {
    static int VisitTime = 12*60*60;//12小时
    static String userdata = "userdata.scms";
    //  给出用户目录,给出用户文件
    public static File GetUserFile(File file) {
        return new File(file.getAbsolutePath()+'/'+userdata);
    }
    public static File GetImageFile(File file){//用户的照片信息
        return new File("D:\\SCMSFILE\\FileManager\\ImageFile"+'/'+file.getName()+".png");
    }
    //    登录服务 -- 比对用户名和密码,成功返回用户数据
    public static ReturnUserData CheckLogin(GetUserData user, HttpServletRequest request){
        ReturnUserData returnUserData = new ReturnUserData("","",""); //初始化内容
        UserFile userFile = OnlineManager.GetUserData(user.getUsername(),0L); //得到用户数据
        if(userFile == null) {
                returnUserData.res = false;
                returnUserData.state = "用户不存在";
                return returnUserData;
        }//返回null代表登录失败
        returnUserData = GetUserData(userFile,user.getPassword());//在用户文件里面查找比对信息

        if(returnUserData.state == "登录成功"){
            //添加数据
            OnlineManager.AddOnlineUser(userFile,1L);

            OnlineManager.AddOnlineDataList(userFile);//填入数据list
            // 颁发签证
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(VisitTime);  //设置持续session时长为12个小时
            session.setAttribute("User",user.getUsername());//存放的是file指针
            System.out.println("签证设置成功");
        }

        return returnUserData;
    }

    //    注册服务 -- 注册创建一个文件,并且存储起来,后面也可以直接设置成登录
    public static ReturnUserData Register(GetUserData user){
        if(UserRBTree.searchFile(user.getUsername())==null){
            File file = UserRBTree.AddItem(user.getUsername());//获得File文件,创建文件,写入文件树,写入UserFile数据
            //拿到数据文件指针,然后放到文件里面去
            ReturnUserData returnUserData = SetUserData(file,user); //这里面负责创建data文件,写入到file文件里面去
            if(returnUserData.state == "注册成功"){
                returnUserData.res = true;
                UserRBTree.sava();
            }else{
                file.delete();
            }
            return returnUserData;
        }else{
            //用户已经存在
            ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"用户已存在");
            returnUserData.res = false;
            return returnUserData;
        }
    }

    //    找到文件中的内容返回
    private static ReturnUserData GetUserData(UserFile userFile,String password){
        // 序列化 - 转换结构
        ReturnUserData res = new ReturnUserData("游客","","");
        if(userFile == null) {
            res.state = "服务器数据出错,请稍后再试";
        } //序列化失败返回null
        else{
            if(!userFile.password.equals(password)) {
                res.state = "密码错误";
            } //密码失败返回null
            else{
                res.state = "登录成功";
                res.netname = userFile.netname;
                res.PersonalWord = userFile.PersonalWord;
                res.owner = new ArrayList<>();
                //根据登录的信息添加一些前端需要的数据
                if(userFile.owner != null){
                    for(int i = 0;i < userFile.owner.size();i++){
                        res.owner.add(((File)userFile.owner.get(i)).getName());
                    }
                }
                res.player = new ArrayList<>();
                if(userFile.player != null){
                    for(int i = 0;i < userFile.player.size();i++){
                        res.player.add(userFile.player.get(i).getName());//把名字传过去
                    }
                }
                res.tips = userFile.notice;
            }
        }
        return res;
    }

    //    写入初始文件,包括网名,组织名称,返回returnUserData
    private static ReturnUserData SetUserData(File file,GetUserData user){
        ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"");
        //把user数据放到userFile中
        UserFile filedata = new UserFile(user.getUsername(),user.getNetname(),user.getPassword(),user.getPersonalword());
//      添加用户数据文件指向
        filedata.file = file;
//      添加本身这个组织
        filedata.owner = new ArrayList<>();
//      构建组织文件
        File dataFile = DatapageManager.AddData(String.valueOf(user.getUsername()));//获得一个指向组织的File文件;
        filedata.owner.add(dataFile);

//      新建数据文件中红黑树指向用户文件
        DataItem dataItem = new DataItem();
        dataItem.users.add(user.getUsername());
//      写入到数据文件
        File dataItemFile = FileManager.NextFile(dataFile,"DataItem");
        try {
            FileOutputStream fileOut = new FileOutputStream(dataItemFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(dataItem);
            out.close();
            fileOut.close();
        } catch (IOException i) {
            returnUserData.state = "注册失败";
            returnUserData.res = false;
        }
//      写入用户文件

        try {
            FileOutputStream fileOut = new FileOutputStream(GetUserFile(file));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(filedata);
            out.close();
            fileOut.close();
            returnUserData.state = "注册成功";
        }catch (Exception e){
            returnUserData.state = "注册失败";
            returnUserData.res = false;
        }
        return returnUserData;
    }

    //  根据file找到UserFile
    static public UserFile GetUser(File file){
        UserFile userFile = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GetUserFile(file)));
            userFile = (UserFile) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            userFile = null;
        }
        return userFile;
    }
    //返回根据long找到的user
    static public UserFile GetUser(Long user){
        File file = UserRBTree.searchFile(user);//读取user位置
        return GetUser(file);
    }
    //  和register配套的保存数据
    static public boolean SaveUser(File file,UserFile user){
        try {
            FileOutputStream fileOut = new FileOutputStream(GetUserFile(file));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }
    // 写入头像图片,返回头像的路径
    static public String SaveImage(byte[] bytes){
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        File file = UserRBTree.searchFile(user.username);
        try {
            FileOutputStream outputStream = new FileOutputStream(GetImageFile(file));
            outputStream.write(bytes);
            outputStream.close();//写入成功
            return "OK";
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //日志系统

    //通知信息添加
    static public ReturnJson AddNotice(List<Long> username, NoticeData notice){
        ReturnJson returnJson = new ReturnAddJson(true,"");
        returnJson.res = true;
        try {
            for (Long user : username){
                //依次寻找目的用户,首先探寻在线树中有没有目标
                UserFile userFile = OnlineManager.GetUserData(user,0L);
                if (userFile == null) {
                    returnJson.res = false;
                    returnJson.state = "用户不存在";
                    break;
                }
                userFile.notice.add(notice);
            }
        }catch (Exception e) {
            returnJson.res = false;
            returnJson.state = "捕获异常";
        }
        return returnJson;
    }

    //修改用户文件,添加组织,接收一个Get,修改文件,给出文件,修改后面看看是不是要放到后面

    //添加组织删除组织

    //添加组织需要注意的一点是,在线树更新后对应的服务器数据需要更新!!!
    static public ReturnJson AddOrg(String org){
        return  new ReturnJson(true,"");
    }
}
