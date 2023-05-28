package scms.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Interceptor.BridgeData;
import scms.Service.DataManager;
import scms.Service.OnlineManager;
import scms.domain.ReturnJson.Return;
import scms.domain.ReturnJson.ReturnEventData;
import scms.domain.ReturnJson.ReturnQueryData;
import scms.domain.ServerJson.ClashTime;
import scms.domain.ServerJson.UserFile;

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

    @RequestMapping("/freetime")
    public Return<ClashTime> QueryFreeTime(@RequestParam("indexID") int indexID,@RequestParam("date") long date,@RequestParam("length") int length){
        // 查询本人indexID 对应的数据页在现有的用户数据下,在date后length天内,存在的空闲时间
        return new DataManager().QueryFreeTime(indexID,date,length);
    }
}
