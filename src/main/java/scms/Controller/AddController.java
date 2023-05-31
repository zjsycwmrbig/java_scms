package scms.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scms.Interceptor.BridgeData;
import scms.Service.DataManager;
import scms.Service.OnlineManager;
import scms.domain.ReturnJson.Return;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.GetJson.GetEventData;
import scms.domain.ServerJson.UserFile;

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


//增加课程数据,请求通过post/put传输一个对象
    @RequestMapping("/item")
    public ReturnAddJson AddItem(@RequestBody GetEventData item){
        DataManager dataManager = new DataManager();
        ReturnAddJson returnJson = dataManager.AddItem(item);
        if (returnJson.res && item.alarmFlag){
            // 可以添加闹钟
            System.out.println("添加闹钟");
            UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);

            user.AddAlarm(item);

        }
        return returnJson;
    }

    // 添加闹钟信息,指定某个事项的重要性
    @RequestMapping("/alarm")
    public  Return<Object> AddAlarm(@RequestParam("key")long key,@RequestParam("indexID") int indexID){
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        if(user.AddAlarm(key,indexID)){
            return new Return<>(true,"添加成功",null);
        }else{
            return new Return<>(false,"闹钟已经存在",null);
        }
    }
}

