package scms.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import scms.Dao.UserRBTree;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        // 1.为线程添加数据,这里添加File类好像不太合适
        HttpSession session = request.getSession();
        Long user = (Long) session.getAttribute("User");

        if(user != null){
            // 2.存在签证
            BridgeData.setRequestInfo(user);
        }
        //这里之后需要拦截不存在的请求，应该返回false
        return true; //返回true表示请求放行
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        在请求处理之后进行处理，注意不要有两次请求
        System.out.print(request.getHeader("content-type")); //输出请求的各种信息，需对应请求格式，感觉日志只需要方法名称

        System.out.println("postHandle Method");
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        System.out.println("调用的方法名为" + handlerMethod.getMethod().getName()); //输出方法的名称

        //根据请求得到学生文件路径
        if(BridgeData.getRequestInfo() == null) return;//如果还没有签证的请求作为废请求,拦截处理
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        if(userFile == null) return;//注册的时候没有userFile

        File UserPath = userFile.file; ////待改动，先通过OnlineTree查找，再使用UserRBTree查找？？？？
        String LogPath = UserPath.getAbsolutePath().concat("\\Log.txt");

        //这里要增加管理员改课程表时候的日志。应该要@Auto
        //根据请求得到一行的日志字符串，格式为 xxxx年xx月xx日 xx时xx分xx秒 “用户名” “操作”
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");
        String timeString =dateTimeFormatter.format(localDateTime);

        ////先根据方法名，用静态方法getFunctionString得到对应操作字符串。后续如果要改成用请求名或者请求名+方法名搭配着用的话，在getFunctionString中修改
        String logString = timeString.concat(" " + userFile.username + " "+ FunctionMatch.getFunctionString(handlerMethod.getMethod().getName()) + "\n");

        //FileWriter文件流，向日志文件中添加字符串
        FileWriter fileWriter = new FileWriter(LogPath,true);
        fileWriter.write(logString);
        fileWriter.close();
        System.out.println("添加日志信息成功");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在请求处理完成之后进行处理，包括异常处理

        BridgeData.clear();
    }
}
