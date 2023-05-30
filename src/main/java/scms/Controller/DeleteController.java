package scms.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import scms.Service.DataManager;
import scms.domain.GetJson.GetEventData;
import scms.domain.ReturnJson.ReturnJson;

/**
 * @author seaside
 * 2023-05-16 20:02
 */
@RestController
@RequestMapping("/delete")
public class DeleteController {

//    @RequestMapping("/item")
//    public ReturnJson DeleteItem(@RequestBody GetEventData item){
//        DataManager dataManager = new DataManager();
//        ReturnJson returnJson = dataManager.deleteItem(item);
//        //本来应该在这里调用日志函数，但是DataManager中才对item的组织进行剖析，所以将调用放在了DataManager的DeleteItem中
//        return returnJson;
//    }

    // 删除条目
    @RequestMapping("/item")
    public ReturnJson DeleteItem(@RequestParam("indexID") int indexID,@RequestParam("begin") long begin){
        if(indexID < 0) return new ReturnJson(false,"无权限删除");

        DataManager dataManager = new DataManager();

        return dataManager.deleteItem(begin,indexID);
    }
}
