package scms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Dao.UserDao;
import scms.Service.UserService;
import scms.domain.UserData;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
//    这里最好返回数字,这样前端更好维护,并且通信成本更低
    @RequestMapping("/login")
    public String CheckLogin(@RequestBody UserData user){
        System.out.println(user.username);
        switch (userService.CheckLogin(user)){
            case 1 : return "登录成功";
            case -1 : return "用户不存在,请先注册!";
            case 0: return "用户名或密码错误";
        }
        return "错误";
    }
    @RequestMapping("/register")
    public boolean CreatUser(@RequestBody UserData user) throws IOException {
        if(userService.CreatUser(user)){
            return true;
        }
        return false;
    }
}
