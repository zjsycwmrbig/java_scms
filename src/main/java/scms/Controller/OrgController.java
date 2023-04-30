package scms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import scms.Dao.DataProcessor;
import scms.Dao.DatabaseRBTree;
import scms.Interceptor.BridgeData;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetOrgInviteData;
import scms.domain.GetJson.GetOrgJionData;
import scms.domain.GetMapJson.NoticeMaker;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.UserFile;

import java.io.File;

/***
 * @author Administrator
 * @date 2023/4/25 13:35
 * @function 有关组织的控制器
 */
@Controller
@RequestMapping("/org")
@ResponseBody
public class OrgController {
    @RequestMapping("/invite")
    public ReturnJson OrgInvite(@RequestBody GetOrgInviteData data){
        NoticeData notice;

        if(data.tips != null){
            notice = NoticeMaker.JoinOrg(data.tips,data.org);
        }else{
             notice = NoticeMaker.JoinOrg(data.org);
        }

        return UserManager.AddNotice(data.userlist,notice);
    }
    //接收org名称
    // -1.org已经存在
    // -2.捕获异常
    @RequestMapping("create")
    public ReturnJson OrgCreate(@RequestParam String org){
        ReturnJson returnJson = new ReturnJson(true,"组织创建成功");
        File file = DatabaseRBTree.AddItem(org); //新建一个组织文件
        if(file == null){
            returnJson.res = false;
            returnJson.state = "组织已存在";
        }else{
            DataProcessor dataProcessor = new DataProcessor(BridgeData.getRequestInfo(),org);
            OnlineManager.NewOnlineData(dataProcessor,1L);
            UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);//用户添加组织信息
            user.owner.add(file);
        }


        return  returnJson;
    }

    @RequestMapping("/jion")
    public ReturnJson OrgJion(@RequestBody GetOrgJionData data){
        //加入某个组织,默认已经通过拦截
        return new ReturnAddJson(true,"");
    }
}
