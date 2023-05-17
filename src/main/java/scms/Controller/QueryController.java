package scms.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Service.DataManager;
import scms.domain.ReturnJson.ReturnEventData;
import scms.domain.ReturnJson.ReturnQueryData;

import java.util.Date;

/***
 * @author zjs
 * @date 2023/3/14 22:48
 * @function 查询接口
 */

@Controller
@RequestMapping("/query")
@ResponseBody
public class QueryController {
    //    查询当下日期一周之内的课程数据,采用GET方式
    @RequestMapping("/now")//查询当下的时间
    public ReturnEventData QueryNow(@RequestParam("date") long datetime){//拿到data的值
        //处理星期数据
        Date date = new Date(datetime);
        DataManager dataManager = new DataManager();//新建一个data
        //日志记录放在了QueryWeek中
        return dataManager.QueryWeek(date);
    }
    @RequestMapping("/all")
    public ReturnEventData QueryAll(){
        DataManager dataManager = new DataManager();
        //日志记录放在了QueryAll中
        return dataManager.QueryAll();
    }

    @RequestMapping("/search")
    public ReturnQueryData QueryKey(@RequestParam("key") String key,@RequestParam("searchmode") int searchmode){
        System.out.println(searchmode);
        DataManager dataManager = new DataManager();
        //日志记录放在了QueryMulti中
        return dataManager.QueryMulti(key);
    }
}
