package scms.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import scms.Service.UserManager;


/***
 * @author Administrator
 * @date 2023/3/31 20:43
 * @function
 */
//上传头像
//
@RequestMapping("/upload")
public class UploadController {
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
