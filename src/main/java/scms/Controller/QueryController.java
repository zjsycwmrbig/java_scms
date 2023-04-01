package scms.Controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Service.DataManager;
import scms.domain.ReturnJson.ReturnEventData;

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
    //    查询当下日期一周之内的课程数据,采用GET方式
    @RequestMapping("/now")//查询当下的时间
    public ReturnEventData QueryNow(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date){//拿到data的值
        //处理星期数据
        DataManager dataManager = new DataManager();//新建一个data
        return dataManager.QueryWeek(date);
    }
}
