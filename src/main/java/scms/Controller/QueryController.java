package scms.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Dao.WriteLog;
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

    // 查询某个课程,或者某个用户的空闲时间
    @RequestMapping("/user_free_time")
    public Return<ClashTime> QueryFreeTime(@RequestParam("key") long user,@RequestParam("date") long date,@RequestParam("length") int length){
        // 用户空闲时间
        DataManager dataManager = new DataManager();
        Return<ClashTime> clashTimeReturn = new Return<>(true,"",dataManager.QueryFreeTime(user,date,length));
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        UserFile userQuery = OnlineManager.GetUserData(user,0L);
        if(userQuery == null) {
            clashTimeReturn.res = false;
            clashTimeReturn.state = "查询用户不存在";
        }
        WriteLog.writeLog(userFile,clashTimeReturn.res,"QueryFreeTime",String.valueOf(user));
        return clashTimeReturn;
    }

    @RequestMapping("/org_free_time")
    public Return<ClashTime> QueryFreeTime(@RequestParam("key") String org,@RequestParam("date") long date,@RequestParam("length") int length){
        // 组织空闲时间
        DataManager dataManager = new DataManager();
        //日志记录放在了QueryFreeTime中
        return dataManager.QueryFreeTime(org,date,length);
    }
}
