package scms.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Service.AddService;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.GetJson.ClassData;
import scms.domain.ReturnJson.ReturnJson;

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
    @RequestMapping("/item")
    public ReturnAddJson AddItem(@RequestBody ClassData item){
        ReturnAddJson returnAddJson = addService.AddItem(item);
        //本来应该在这里调用日志函数，但是DataManager中才对item的组织进行剖析，所以将调用放在了DataManager的AddItem中
        return returnAddJson;//这里往哪里加入,看index
    }

}

