package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.domain.UserData;

import java.io.File;
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
    public String GetbyName(String name){
//        fir 判断是否存在该用户
        return "password";
    }

    public boolean CreatUser(UserData user) throws IOException {
//        创建
        if(scms==null)scms = new SCMSFILE(user.getUsername());
        System.out.println("DEBUG");
        System.out.println(this.scms.f.getAbsoluteFile());
        this.scms.f.mkdir();
        if(this.scms.mkdir())return true;
        else return false;
    }
}
