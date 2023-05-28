package scms.Service;

import org.springframework.stereotype.Service;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.GetJson.GetEventData;

/***
 * @author zjs
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class AddService extends scms.Service.Service {
    DataManager dataManager;
    //  新添加的节点
    public ReturnAddJson AddItem(GetEventData item){
        dataManager = new DataManager();
        ReturnAddJson returnAddJson = dataManager.AddItem(item);
        return returnAddJson;
    }
}
