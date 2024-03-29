package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;
import scms.Interceptor.BridgeData;
import scms.Dao.WriteLog;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetUserData;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
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
        WriteLog.writeUserLog(userFile,returnUserData.res,"CheckLogin","");
        return returnUserData;
    }

//    注册请求
    @RequestMapping("/register")
    public ReturnUserData CreatUser(@RequestBody GetUserData user) throws IOException {
        ReturnUserData returnUserData = UserManager.Register(user);
        UserFile userFile = OnlineManager.GetUserData(user.getUsername(),0L);
        System.out.println(userFile.file);
        WriteLog.writeUserLog(userFile,returnUserData.res,"CreatUser","");
        return returnUserData;
    }
//    登出请求
    @RequestMapping("/logout")
    public ReturnJson LogOut(){
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);//要用Bridge获取一下用户，但是是GetUserData类吗
        ReturnJson returnJson = OnlineManager.RemoveOnline(BridgeData.getRequestInfo());
        WriteLog.writeUserLog(userFile,returnJson.res,"LogOut","");
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
    // 重命名,更改name条目
    @RequestMapping("/change_name")
    public ReturnJson rename(@RequestParam("itemname") String name){
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        userFile.netname = name;
        ReturnJson returnJson = new ReturnJson(true,"更改成功");
        WriteLog.writeUserLog(userFile,returnJson.res,"rename",name);
        return returnJson;
    }
    // 更改个性签名
    @RequestMapping("/change_word")
    public ReturnJson changePersonalWord(@RequestParam("itemname") String word){
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        userFile.PersonalWord = word;
        ReturnJson returnJson = new ReturnJson(true,"更改成功");
        WriteLog.writeUserLog(userFile,returnJson.res,"changePersonalWord",word);
        return returnJson;
    }
    // 拿到最新的用户信息,专指更新组织的情况
//    @RequestMapping("/fetch")
//    public ReturnUserData fetch(){
//        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
//        ReturnUserData returnUserData = new ReturnUserData(userFile.netname,userFile.PersonalWord,"",userFile.hasImage);
//        // owner
//        for (File owner:userFile.owner){
//            returnUserData.owner.add(owner.getName());
//        }
//        for(File player:userFile.player){
//            returnUserData.player.add(player.getName());
//        }
//        for(File owmer:userFile.owner){
//            returnUserData.passwords.add(password.getName());
//        }
//        return returnUserData;
//    }
}
