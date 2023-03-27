package scms.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;
import org.apache.coyote.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import scms.Dao.Dao;
import scms.Dao.SCMSFILE;

/***
 * @author Administrator
 * @date 2023/3/27 21:34
 * @function
 */

@Component
public class ScmsInterceptor implements HandlerInterceptor {
    SCMSFILE scms;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求处理之前进行处理,这里添加拦截

//        1.为线程添加数据
        HttpSession session = request.getSession();
        scms = (SCMSFILE) session.getAttribute("SCMSFILE");

        BridgeData.setRequestInfo(scms);//可能没用,因为scms不一定存在

        return true; // 返回true表示继续执行请求处理
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 在请求处理之后进行处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求处理完成之后进行处理，包括异常处理
        if(BridgeData.getRequestInfo() == null){
            System.out.println("未登录或者注册请求");
        }else{
            System.out.println(BridgeData.getRequestInfo());
        }
//        序列化存储文件

        BridgeData.clear();
    }
}
