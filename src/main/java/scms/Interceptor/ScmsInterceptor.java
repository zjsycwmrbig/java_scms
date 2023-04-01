package scms.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import scms.Service.UserManager;
import scms.domain.ServerJson.UserFile;

import java.io.File;

/***
 * @author Administrator
 * @date 2023/3/27 21:34
 * @function
 */

@Component
public class ScmsInterceptor implements HandlerInterceptor {
    UserFile file;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        1.为线程添加数据
        HttpSession session = request.getSession();
        File filePoint = (File) session.getAttribute("User");
        if(filePoint != null){
//            存在签证
            file = UserManager.GetUser(filePoint);
            BridgeData.setRequestInfo(file);
            System.out.println(file.toString());
        }
        //这里之后需要拦截不存在的请求
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在请求处理之后进行处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求处理完成之后进行处理，包括异常处理

        BridgeData.clear();
    }
}
