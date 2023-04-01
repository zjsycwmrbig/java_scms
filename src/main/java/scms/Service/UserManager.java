package scms.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import scms.Dao.UserRBTree;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.GetJson.GetUserData;
import scms.domain.ServerJson.UserFile;

import java.io.*;
import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/30 9:43
 * @function 静态类管理用户信息 用户权限的服务类
 */
public class UserManager {
    static int VisitTime = 12*60*60;//12小时
    static String userimage = "userimage.png";
    static String userdata = "userdata.scms";

    //  给出用户目录,给出用户文件
    public static File GetUserFile(File file) {
        return new File(file.getAbsolutePath()+'/'+userdata);
    }
    public static File GetImageFile(File file){
        return new File(file.getAbsolutePath()+'/'+userimage);
    }
//    登录服务 -- 比对用户名和密码,成功返回用户数据
    public static ReturnUserData CheckLogin(GetUserData user, HttpServletRequest request){
        UserRBTree.Init(); //读取二叉树
        ReturnUserData returnUserData = new ReturnUserData("","",""); //初始化内容
        File file = UserRBTree.searchFile(user.getUsername());
        if(file == null) {
            returnUserData.state = "用户不存在";
        }//返回null代表登录失败
        else{
            //找到用户文件
            returnUserData = GetUserData(file,user.getPassword());//在用户文件里面查找比对信息
            if(returnUserData.state == "登录成功"){
                // 颁发签证
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(VisitTime);  //设置持续session时长为12个小时
                session.setAttribute("User",file);
            }
        }
        return returnUserData;
    }
//    注册服务 -- 注册创建一个文件,并且存储起来
    public static ReturnUserData Register(GetUserData user){
        UserRBTree.Init();
        if(UserRBTree.searchFile(user.getUsername())==null){
            File file = UserRBTree.AddItem(user.getUsername());//获得File文件,创建文件,写入文件树,写入UserFile数据
            //拿到数据文件指针,然后放到文件里面去
            ReturnUserData returnUserData = SetUserData(file,user);//这里面负责创建data文件,写入到file文件里面去
            if(returnUserData.state == "注册成功"){
                UserRBTree.sava();
            }
            return returnUserData;
        }else{
            ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"用户已存在");
            return returnUserData;
        }
    }
//    找到文件中的内容返回
    private static ReturnUserData GetUserData(File file,String password){
        // 序列化 - 转换结构
        ReturnUserData res = new ReturnUserData("游客","","");
        UserFile userFile = GetUser(file);
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

    //    写入初始文件,包括网名,组织名称
    private static ReturnUserData SetUserData(File file,GetUserData user){
//        把user数据放到userFile中
        UserFile filedata = new UserFile(user.getUsername(),user.getNetname(),user.getPassword(),user.getPersonalword());
//        添加本身这个组织
        filedata.owner = new ArrayList<>();
        filedata.owner.add(DatabaseManager.AddData(String.valueOf(user.getUsername())));//添加一个指向组织的File文件;
        ReturnUserData returnUserData = new ReturnUserData(user.getNetname(),user.getPersonalword(),"");
        try {
            FileOutputStream fileOut = new FileOutputStream(GetUserFile(file));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(filedata);
            out.close();
            fileOut.close();
            returnUserData.state = "注册成功";
        }catch (Exception e){
            returnUserData.state = "注册失败";
        }
        return returnUserData;
    }

//  根据file找到file
    static public UserFile GetUser(File file){
        UserFile userFile = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(GetUserFile(file)));
            userFile = (UserFile) ois.readObject();
            ois.close();
        } catch (Exception e) {
            userFile = null;
        }
        return userFile;
    }
    //    修改用户文件,添加组织,接收一个Get,修改文件,给出文件,修改后面看看是不是要放到后面

    //    添加组织删除组织
}
