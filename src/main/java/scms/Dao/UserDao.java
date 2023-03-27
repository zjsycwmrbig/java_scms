package scms.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import scms.domain.UserData;

import java.io.*;


@Component
public class UserDao extends Dao{
//    这个东西是为了后面别的请求设置签证,后期也可以做请求拦截
    private void InitScms(HttpServletRequest request,SCMSFILE scms){
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(12*60*60);  //设置持续session时长为12个小时
        session.setAttribute("SCMSFILE",scms);
    }
//    验证登录

//    修改checklogin逻辑
    public int CheckLogin(UserData user,HttpServletRequest request) throws IOException {
        scms = new SCMSFILE(user.getClassName(), user.getUsername());//找到文件

        if(!scms.exists()) return -1;//用户不存在
        else{
            JSON = new ObjectMapper();
//            UserData ser = JSON.readValue(scms.userData,UserData.class); //读取用户信息文件
            try {
                // 创建一个ObjectInputStream对象
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(scms.userData));
                UserData ser = (UserData) ois.readObject();
                ois.close();
                if(ser.getPassword().equals(user.getPassword())){
//                验证成功
//                发送签证
                    InitScms(request,scms);
                    return 1;
                }else{
                    return 0;
                }
            } catch (Exception e) {
                return -2;
            }
        }
    }
//    创建用户
    public int CreatUser(UserData user) throws IOException {
//      创建一个scms文件
        scms = new SCMSFILE(user.getClassName(),user.getUsername());
        if(scms.exists()){
//            用户已存在
            return -1;
        }else {
//            注册用户
            if(scms.creat() != 1) {
                scms.studentDirectory.delete();
                return 0;
            }
//            增加用户信息到文件
            try {
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(scms.userData));
                oos.writeObject(user);
                // 关闭流
                oos.close();
            } catch (Exception e) {
                e.printStackTrace();
//                出错 注册失败

                return 3;
            }

//            ArrayList<ClassData> ClassList = new ArrayList<>();
////                先初始化class文件
//            if(flag == 0){
//                FileOutputStream file = new FileOutputStream(scms.courseData);
//                JSON.writeValue(file,ClassList);
//                file.close();
//            }
//
//            if (flag != -1) {
//                FileOutputStream file = new FileOutputStream(scms.userData);
//                JSON.writeValue(file,user);
//                file.close();
////              初始化文件
//                FileOutputStream schefile = new FileOutputStream(scms.activityData);
//                JSON.writeValue(schefile,ClassList);
//                schefile.close();
//                return 1;
//            }else{
////                失败记得删除掉这个目录
//                scms.studentDirectory.delete();
//            }
            return 1;
        }
    }
}
