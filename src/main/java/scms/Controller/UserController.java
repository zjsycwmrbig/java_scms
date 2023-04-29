package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import scms.Interceptor.BridgeData;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetUserData;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
//    登录请求
//    这里最好返回数字,这样前端更好维护,并且通信成本更低
    @RequestMapping("/login")
    public ReturnUserData CheckLogin(@RequestBody GetUserData user, HttpServletRequest request) throws IOException {

        return UserManager.CheckLogin(user,request);
    }

//    注册请求
    @RequestMapping("/register")
    public ReturnUserData CreatUser(@RequestBody GetUserData user) throws IOException {
        return UserManager.Register(user);
    }
//    登出请求
    @RequestMapping("/logout")
    public ReturnJson LogOut(){
        return OnlineManager.RemoveOnline(BridgeData.getRequestInfo().username);
    }
}
