package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Service.QueryService;
import scms.domain.ClassData;
import scms.domain.UserData;

import java.io.IOException;
import java.util.Date;


/***
 * @author zjs
 * @date 2023/3/14 22:48
 * @function
 */
@Controller
@RequestMapping("/query")
@ResponseBody
public class QueryController {
    @Autowired
    QueryService queryService;
//    查询该某个用户的全部信息
//    这一个接口有问题,日后再完善
    @RequestMapping("/all/{classname}/{username}")
    public ClassData[] QueryAll(@PathVariable("username")String user,@PathVariable("classname")String clas) throws IOException {
        return queryService.QueryAll(clas,user);
    }
//    查询当下日期一周之内的课程数据,采用GET方式
    @RequestMapping("/now")
    public ClassData[] QueryNow(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,HttpServletRequest request){
        System.out.println(date);
        HttpSession session = request.getSession();
        return queryService.QueryNow(date,session);
    }
}
