package scms.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import scms.domain.UserData;
import java.io.FileOutputStream;
import java.io.IOException;


@Component
public class UserDao extends Dao{
//    验证登录
    public int CheckLogin(UserData user) throws IOException {
        scms = new SCMSFILE(user.getUsername());
        if(!scms.exists())return -1;
        else{
            JSON = new ObjectMapper();
            UserData ser = JSON.readValue(scms.userdata,UserData.class);
            if(ser.getPassword().equals(user.getPassword())){
                return 1;
            }else{
                return 0;
            }
        }
    }
//    创建用户
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
                JSON = new ObjectMapper();
                FileOutputStream file = new FileOutputStream(scms.userdata);
                JSON.writeValue(file,user);
                file.close();
                return 1;
            }
            else return 0;
        }
    }
}
