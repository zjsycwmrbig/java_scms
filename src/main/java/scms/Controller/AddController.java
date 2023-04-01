package scms.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Service.AddService;
import scms.domain.ServerJson.ClashErrorData;
import scms.domain.GetJson.ClassData;

/***
 * @author Administrator
 * @date 2023/3/8 15:10
 * @function
 */
// 增删改查中的 增操作
@RestController
@RequestMapping("/add")
public class AddController {
    //    登录请求
    @Autowired
    AddService addService;

//增加课程数据,请求通过post/put传输一个对象
    @RequestMapping("/class")
    public ClashErrorData CheckLogin(@RequestBody ClassData item, HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (addService.CheckLogic(item,session));
    }
//  添加日程,添加到个人的文件里面
    @RequestMapping("/schedule")
    public ClashErrorData AddSchedule(@RequestBody ClassData item, HttpServletRequest request){
//        获得session对象
        HttpSession session = request.getSession();
        return (addService.CheckLogic(item,session));
    }
    @RequestMapping("/item")
    public  boolean AddItem(@RequestBody ClassData item){
        return addService.AddItem(item,0);
    }

}

