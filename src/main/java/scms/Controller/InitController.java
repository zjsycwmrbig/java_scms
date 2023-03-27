package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/***
 * @author Administrator
 * @date 2023/3/8 16:30
 * @function
 */
@RestController
public class InitController {
    @RequestMapping("/init")
    public int init(HttpServletRequest request){
//        设置session
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(12*60*60);  //设置持续session时长
        session.setAttribute("username","name");
        return 1;
    }
}
