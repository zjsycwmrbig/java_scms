package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
        System.out.println(user.getUsername());
        switch (userService.CheckLogin(user)){
            case 1 : {
//              拿到session,创建一个session,创建一个session就需要一个request
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30*60);
                session.setAttribute("username",user.getUsername());
                return (String) session.getAttribute("username");
            }
            case -1 : return "用户不存在,请先注册!";
            case 0: return "用户名或密码错误";
        }
        return "错误";
    }
//    注册请求
    /*
    * return 0 注册失败
    * return 1 注册成功
    * return -1 注册用户已存在
    *
    * */
    @RequestMapping("/register")
    public int CreatUser(@RequestBody UserData user) throws IOException {
        return userService.CreatUser(user);
    }
}
