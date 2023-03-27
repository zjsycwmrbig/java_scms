package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Dao.InitDao;
import scms.Service.UserService;
import scms.domain.UserData;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
//    登录请求
    @Autowired
    UserService userService;
//    这里最好返回数字,这样前端更好维护,并且通信成本更低
    @RequestMapping("/login")
    public String CheckLogin(@RequestBody UserData user, HttpServletRequest request) throws IOException {
        System.out.println(user.getUsername() + " " + user.getClassName());
        switch (userService.CheckLogin(user,request)){
            case 1 : {
//              拿到session,创建一个session,创建一个session就需要一个request
//              创建一个有关user的session
                return "登录成功";
            }
            case -1 : return "用户不存在,请先注册!";
            case 0: return "用户名或密码错误";
        }
        return "错误";
    }
//    注册请求
    @RequestMapping("/register")
    public int CreatUser(@RequestBody UserData user) throws IOException {
        return userService.CreatUser(user);
    }
}
