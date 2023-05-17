package scms.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Service.DataManager;
import scms.domain.GetJson.ClassData;
import scms.domain.ReturnJson.ReturnJson;

/**
 * @author seaside
 * 2023-05-16 20:02
 */
@RestController
@RequestMapping("/delete")
public class DeleteController {

    @RequestMapping("/item")
    public ReturnJson DeleteItem(@RequestBody ClassData item){
        DataManager dataManager = new DataManager();
        ReturnJson returnJson = null;
        dataManager.deleteItem(item);
        return returnJson;
    }
}
