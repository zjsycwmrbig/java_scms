package scms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Service.AddService;
import scms.Service.UserService;
import scms.domain.ClassData;
import scms.domain.UserData;

import java.io.IOException;

/***
 * @author Administrator
 * @date 2023/3/8 15:10
 * @function
 */
//add请求
@RestController
@RequestMapping("/add")
public class AddController {
    //    登录请求
    @Autowired
    AddService addService;
//增加课程数据,请求通过post/put传输一个对象
    @RequestMapping("/class")
    public boolean CheckLogin(@RequestBody ClassData item) {
        if(addService.CheckLogic(item)) return true;
        else return false;
    }
}

