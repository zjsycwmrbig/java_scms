package scms.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Dao.DataProcessor;
import scms.Interceptor.BridgeData;
import scms.Service.DataManager;
import scms.Service.OnlineManager;
import scms.domain.GetJson.GetEventData;
import scms.domain.ReturnJson.Return;
import scms.domain.ReturnJson.ReturnEventData;
import scms.domain.ReturnJson.ReturnQueryData;
import scms.domain.ServerJson.ClashTime;
import scms.domain.ServerJson.EventItem;
import scms.domain.ServerJson.UserFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    // 查询某个用户的课程数据 - 通过关键词查询
    @RequestMapping("/search")
    public Return<List<EventItem>> QueryKey(@RequestParam("key") String key,@RequestParam("searchmode") int searchmode){
        System.out.println(searchmode);
        DataManager dataManager = new DataManager();
        ReturnQueryData res;
        if(searchmode == 1){
            // 日志记录放在了QueryMulti中
             res = dataManager.QueryMulti(key);
        }
        else {
            res = dataManager.QueryExact(key);
        }
        // 添加闹钟信息

        // 返回的成为了一个EventItem列表,和query统一标准
        return new Return<>(true,"",toEventList(res,dataManager));
    }
    // 工具类
    public List<EventItem> toEventList(ReturnQueryData data,DataManager dataManager){
        List<EventItem> list = new ArrayList<>();
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        for (int i = 0; i < data.list.size(); i++) {
            // 获得item
            GetEventData item = data.list.get(i).item;
            DataProcessor dataProcessor = null;
            if(item.indexID >= 0){
                // 自己的事件
                dataProcessor = dataManager.owner.get(item.indexID);
            }else{
                dataProcessor = dataManager.player.get(-item.indexID-1);
            }
            // 转换为EventItem

            long begin = item.begin;
            for(int j = 0;j < item.size; begin += item.circle * 24 * 60 * 60 *1000L){
                if(dataProcessor.IsExist(begin)) {
                    // 存在
                    j++;
                    EventItem eventItem = new EventItem(item.type, item.title, item.location, begin, item.length, item.locationData, item.indexID,item.group);
                    eventItem.alarmFlag = user.Exist(eventItem.begin);
                    list.add(eventItem);
                }
            }

        }
        return list;
    }


    // 查询从begin到end的课程数据
    @RequestMapping("/between")
    public Return<List<EventItem>> QueryBetween(@RequestParam("begin") long begin,@RequestParam("end") long end){
        DataManager dataManager = new DataManager();
        return dataManager.QueryBetween(begin,end);
    }

    // 查询某个课程,或者某个用户的空闲时间
    @RequestMapping("/user_free_time")
    public Return<List<HashMap<String,Long>>> QueryFreeTime(@RequestParam("key") long user,@RequestParam("date") long date,@RequestParam("length") int length){
        // 用户空闲时间
        DataManager dataManager = new DataManager();
        Return<ClashTime> freeTime = dataManager.QueryFreeTime(user,date,length);
        return new Return<>(freeTime.res,freeTime.state,toHashMap(freeTime.data));
    }

    @RequestMapping("/org_free_time")
    public Return<List<HashMap<String,Long>>> QueryFreeTime(@RequestParam("key") String org, @RequestParam("date") long date, @RequestParam("length") int length){
        // 组织空闲时间
        DataManager dataManager = new DataManager();
        Return<ClashTime> freeTime = dataManager.QueryFreeTime(org,date,length);
        return new Return<>(freeTime.res,freeTime.state,toHashMap(freeTime.data));
    }


    // 工具类
    public List<HashMap<String,Long>> toHashMap(ClashTime data) {
        List<HashMap<String,Long>> list = new ArrayList<>();
        for(int i = 0;i < data.begins.size();i++) {
            HashMap<String,Long> map = new HashMap<>();
            map.put("begin",data.begins.get(i));
            map.put("end",data.ends.get(i));
            list.add(map);
        }
        return list;
    }
}