package scms.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import scms.Dao.DataProcessor;
import scms.Interceptor.BridgeData;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ServerJson.ClashErrorData;
import scms.domain.GetJson.ClassData;
import scms.domain.ServerJson.UserFile;

import java.util.ArrayList;

/***
 * @author zjs
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class AddService extends scms.Service.Service {
    DataManager dataManager;
    UserFile user;
    //  新添加的节点
    public ReturnAddJson AddItem(ClassData item){
        dataManager = new DataManager();
        ReturnAddJson returnAddJson = dataManager.AddItem(item);
        return returnAddJson;
    }
}
