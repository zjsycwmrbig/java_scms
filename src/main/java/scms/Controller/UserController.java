package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import scms.Interceptor.BridgeData;
import scms.Interceptor.WriteLog;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetUserData;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.ServerJson.UserFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {
//    登录请求
//    这里最好返回数字,这样前端更好维护,并且通信成本更低
    @RequestMapping("/login")
    public ReturnUserData CheckLogin(@RequestBody GetUserData user, HttpServletRequest request) throws IOException {
        ReturnUserData returnUserData = UserManager.CheckLogin(user,request);
        UserFile userFile = OnlineManager.GetUserData(user.getUsername(),0L);
        WriteLog.writeUserLog(userFile,returnUserData.res,"CheckLogin");
        return returnUserData;
    }

//    注册请求
    @RequestMapping("/register")
    public ReturnUserData CreatUser(@RequestBody GetUserData user) throws IOException {
        ReturnUserData returnUserData = UserManager.Register(user);
        UserFile userFile = OnlineManager.GetUserData(user.getUsername(),0L);
        WriteLog.writeUserLog(userFile,returnUserData.res,"CreatUser");
        return returnUserData;
    }
//    登出请求
    @RequestMapping("/logout")
    public ReturnJson LogOut(){
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);//要用Bridge获取一下用户，但是是GetUserData类吗
        ReturnJson returnJson = OnlineManager.RemoveOnline(BridgeData.getRequestInfo());
        WriteLog.writeUserLog(userFile,returnJson.res,"LogOut");
        return returnJson;
    }

    @RequestMapping("/upload")
    public String UploadImage(@RequestBody MultipartFile file){
        try {
            // 处理文件上传操作
            byte[] bytes = file.getBytes(); //拿到文件
            return UserManager.SaveImage(bytes);
        } catch (Exception e) {
            return "Error uploading file.";
        }
    }

}
