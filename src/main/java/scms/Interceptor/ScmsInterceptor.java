package scms.Interceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
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
        //这里用response对返回进行判断
        //response无法调用getWriter方法,提示getOutputStream() has already been called for this response
        //可能现在只能研究下getOutputStream这个方法怎么得到Json了
        /*ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getOutputStream().toString());
        JsonNode dataNode = jsonNode.get("data");

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = new ByteArrayInputStream(outputStream.toString().getBytes());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        String responseString = byteArrayOutputStream.toString();
        System.out.println(responseString);
        String jsonString = new String(buffer,StandardCharsets.UTF_8);
        //试着将byte[]数组转换为json对象 */
        if(CustomResponseBodyAdvice.returnMark == 0) {
            System.out.println("方法调用失败");
            return;
        }
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

@ControllerAdvice
@Component
class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    static int returnMark = 0; //为零表示失败
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // 支持所有类型的返回值
    }

    @Override
    public Object beforeBodyWrite (Object body, MethodParameter returnType, MediaType selectedContentType,
                                   Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                   ServerHttpRequest request, ServerHttpResponse response) {
        if (selectedContentType != null && selectedContentType.includes(MediaType.APPLICATION_JSON)) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.valueToTree(body);
            if(jsonNode == null || !jsonNode.has("res"))
                return body;
            if(jsonNode.get("res").asBoolean())
                returnMark = 1;
            else
                returnMark = 0;

            return jsonNode;
        }
        return body;
    }
}
/*在上面的代码中，我们创建了一个名为CustomResponseBodyAdvice的ResponseBodyAdvice实现类，并将其标记为@ControllerAdvice，
这意味着它将适用于所有控制器。我们实现了supports和beforeBodyWrite两个方法，supports方法返回true，
表示该拦截器支持所有类型的返回值。在beforeBodyWrite方法中，我们首先检查响应类型是否为json，
然后使用ObjectMapper将响应内容转换为JsonNode对象，然后使用get("res")方法获取返回值。最后，我们原封不动地将JsonNode对象返回。*/
