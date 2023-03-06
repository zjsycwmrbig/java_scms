package scms.Dao;

import org.springframework.stereotype.Component;
import scms.domain.UserData;
import java.io.IOException;

@Component
public class UserDao {
    SCMSFILE scms;//文件指针,指向目录
//    第一步登录,初始化scms
    public boolean CheckData(String name){
        scms = new SCMSFILE(name);
//        存在返回1,不存在返回0,这里要关闭吗
        if(!scms.exists())return false;
        else return true;
    }
//      把密码取出来
    public String GetPassword(String name){
//        fir 判断是否存在该用户
        return "password";
    }

    public int CreatUser(UserData user) throws IOException {
//        创建
        scms = new SCMSFILE(user.getUsername());
        if(scms.exists()){
//            用户已存在
            return -1;
        }else {
            if (scms.creat()) {
//          创建一套用户文件      scms.WritePassword(user.getpassword);
//                添加userdata数据
                return 1;
            }
            else return 0;
        }
    }
}
