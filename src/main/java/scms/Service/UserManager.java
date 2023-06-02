package scms.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import scms.Dao.DataProcessor;
import scms.Dao.DatabaseManager;
import scms.Dao.UserRBTree;
import scms.Interceptor.BridgeData;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.GetJson.GetUserData;
import scms.domain.ServerJson.NoticeData;
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
        ReturnUserData returnUserData = new ReturnUserData("","","",false); //初始化内容

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
            // 添加组织口令信息
            for(int i = 0;i < userFile.owner.size();i++){
                returnUserData.passwords.add(OnlineManager.GetEventData(userFile.owner.get(i).getName(),0L).dataItem.password);
            }
            // 头像信息
            returnUserData.hasImage = userFile.hasImage;
            returnUserData.dataUser = OnlineManager.AddOnlineDataList(userFile);//填入数据list,返回数据用户组
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
        if(UserRBTree.searchFile(user.getUsername()) == null){
            File file = UserRBTree.AddItem(user.getUsername()); //获得File文件,创建文件,写入文件树,写入UserFile数据
            if (file == null) {//如果文件创建失败
                ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"服务器数据出错,请稍后再试",false);
                returnUserData.res = false;
                return returnUserData;
            }
            //拿到数据文件指针,然后放到文件里面去
            ReturnUserData returnUserData = SetUserData(file,user); //这里面负责创建data文件,写入到file文件里面去
            if(returnUserData.state == "注册成功"){
                returnUserData.res = true;
            }else{
                file.delete();
            }
            return returnUserData;
        }else{
            //用户已经存在
            ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"用户已存在",false);
            returnUserData.res = false;
            return returnUserData;
        }
    }

    //    找到文件中的内容返回
    private static ReturnUserData GetUserData(UserFile userFile,String password){
        // 序列化 - 转换结构
        ReturnUserData res = new ReturnUserData("游客","","",false);
        if(userFile == null) {
            res.state = "服务器数据出错,请稍后再试";
        } //序列化失败返回null
        else{
            if(!userFile.password.equals(password)) {
                res.state = "密码错误";
            } //密码失败返回null
            else{
                res.state = "登录成功";
                res.username = userFile.username;
                res.netname = userFile.netname;
                res.PersonalWord = userFile.PersonalWord;
                res.owner = new ArrayList<>();
                res.passwords = new ArrayList<>();
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
            }
        }
        return res;
    }

    //    写入初始文件,包括网名,组织名称,返回returnUserData
    private static ReturnUserData SetUserData(File file,GetUserData user){
        ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"注册成功",false);
        //把user数据放到userFile中
        UserFile fileData = new UserFile(user.getUsername(),user.getNetname(),user.getPassword(),user.getPersonalword());
//      添加用户数据文件指向
        fileData.file = file;

        fileData.hasImage = false;
//      添加本身这个组织
        fileData.owner = new ArrayList<>();
//      构建组织文件
        File dataFile = DatabaseManager.AddItem(user.getUsername() + "的个人空间");
        fileData.owner.add(dataFile);
//      新建数据文件中红黑树指向用户文件
        DataProcessor dataProcessor = new DataProcessor(user.getUsername(),user.getUsername() + "的个人空间",dataFile);
        dataProcessor.dataItem.type = 0;//更新type的值
//      默认网名
        fileData.netname = RandomNetname();
        OnlineManager.AddOnlineUser(fileData,0L);//添加缓存
        OnlineManager.AddOnlineData(dataProcessor,0L);//添加数据

        System.out.println("注册成功");
        return returnUserData;
    }
    // 写入头像图片,返回头像的路径
    static public String SaveImage(byte[] bytes){
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        File file = user.file;
        try {
            FileOutputStream outputStream = new FileOutputStream(GetImageFile(file));
            outputStream.write(bytes);
            outputStream.close();//写入成功
            user.hasImage = true;
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

            }
        }catch (Exception e) {
            returnJson.res = false;
            returnJson.state = "捕获异常";
        }
        return returnJson;
    }



    // 工具类
    // 随机生成网名和个性签名

    private static String RandomNetname(){
        String[] names = {"令狐冲","林平之","曲洋","任盈盈","向问天","任我行","冲虚","方正","岳不群","宁中则","林平之","岳灵珊","宇智波斑","漩涡鸣人","何同学","元始天尊","镇元大仙","太上老君","通天教主","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星","太上老君","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星","太上老君","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星","太上老君","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星","太上老君","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星","太上老君","玉鼎真人","九天玄女","南极仙翁","北极仙翁","西王母","东王公","南王子","北王子","太白金星"};
        return names[(int)(Math.random()*names.length)];
    }
    //通知信息删除
//    static public ReturnJson RemoveNotice(NoticeData notice){
//        ReturnJson res = new ReturnJson(true,"");
//        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
//        res.res = user.notice.remove(notice);
//        return res;
//    }

    //通知信息忽略
//    static public ReturnJson IgnoreNotice(NoticeData notice){
//        ReturnJson res = new ReturnJson(true,"");
//        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
//        boolean flag = false;
//        for(NoticeData i: user.notice){
//            if(i == notice && !i.isIgnore){
//                i.isIgnore = true;
//                flag = true;
//                break;
//            }
//        }
//        if (!flag) res.res = false;
//        return res;
//    }
    //修改用户文件,添加组织,接收一个Get,修改文件,给出文件,修改后面看看是不是要放到后面

    //添加组织删除组织

    //添加组织需要注意的一点是,在线树更新后对应的服务器数据需要更新!!!

}
