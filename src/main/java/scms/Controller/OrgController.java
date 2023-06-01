package scms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import scms.Dao.DataProcessor;
import scms.Dao.DatabaseManager;
import scms.Interceptor.BridgeData;
import scms.Dao.WriteLog;
import scms.Service.DataManager;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetOrgInviteData;
import scms.domain.GetMapJson.NoticeMaker;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.util.List;

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
        ReturnJson returnJson = UserManager.AddNotice(data.userlist,notice);
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);

        WriteLog.writeLog(userFile,returnJson.res,"OrgInvite",data.org);
        for (int i = 0; i < data.userlist.size(); i++) {
            userFile = OnlineManager.GetUserData(data.userlist.get(i), 0L);
            if (userFile != null) {
                //WriteLog.writeOrgLog(userFile,returnJson.res,data.org,"被邀请进入组织");
                WriteLog.writeLog(userFile,returnJson.res,"被邀请进入组织",data.org);
            }
        }//被邀请的人也要在日志中记录
        return returnJson;
    }
    //接收org名称
    // -1.org已经存在
    // -2.捕获异常
    @RequestMapping("create")
    public ReturnJson OrgCreate(@RequestParam String org){
        ReturnJson returnJson = new ReturnJson(true,"组织创建成功");
        File file = DatabaseManager.AddItem(org); //新建一个组织文件
        if (file == null){
            returnJson.res = false;
            returnJson.state = "组织已存在";
            return returnJson;
        }

        System.out.println("在创建组织中,首先创建文件" + file.getAbsolutePath() + " " + file.getName());
        System.out.println("---");
        System.out.println(DatabaseManager.searchFile(org).getAbsolutePath());
        if(file == null){
            returnJson.res = false;
            returnJson.state = "组织已存在";
        }else{
            UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);//用户添加组织信息
            user.owner.add(file); //添加组织信息
            DataProcessor dataProcessor = new DataProcessor(BridgeData.getRequestInfo(),org,file);
            System.out.println("创建一个dataProcessor");
            dataProcessor.print();
            // indexID
            dataProcessor.dataItem.type = user.owner.size()-1;
            // 更新一个type
            System.out.println("添加组织信息");
            OnlineManager.NewOnlineData(dataProcessor,1L);//直接新建一个

            System.out.println();
            //WriteLog.writeOrgLog(user,returnJson.res,org,"OrgCreate"); //添加日志
            WriteLog.writeLog(user,returnJson.res,"OrgCreate",org);
        }
        return  returnJson;
    }

    //接收org名称
    // -1. 组织不存在
    // -2. 组织中存在冲突
    @RequestMapping("/join")
    public ReturnJson OrgJoin(@RequestParam String org){
        //加入某个组织,默认已经通过拦截
        ReturnJson res =  new ReturnAddJson(true,"加入成功");
        DataManager dataManager = new DataManager();
        DataProcessor Org = OnlineManager.GetEventData(org,0L);
        if(Org == null){
            res.res = false;res.state = "组织不存在";
        }else{
            //判断该组织是否为私人组织
            if(Org.dataItem.type == 0){
                res.res = false;
                res.state = "组织为私人组织,不可加入";
            }else{
                res = dataManager.AddOrg(Org); //先获得组织冲突文件,如果成功在这里就添加到用户和组织信息里面做双向连接
            }
            if(res.res){
                OnlineManager.GetEventData(org,1L);//给这份文件添加权重
            }
        }
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        //WriteLog.writeOrgLog(userFile,res.res,org,"OrgJoin");
        WriteLog.writeLog(userFile, res.res, "OrgJoin",org);
        return res;
    }

    @RequestMapping("/delete")
    public ReturnJson OrgDelete(@RequestParam String org){
        //删除某个组织
        ReturnJson res = new ReturnJson(true,"组织删除成功");
        DataProcessor Org = OnlineManager.GetEventData(org,0L);
        List<Long> userAll = null;
        if(Org == null) {
            res.res = false;
            res.state = "组织不存在";
        }
        else {
            userAll = Org.dataItem.users; //得到组织中所有用户的信息
            for (int i = 0; i < userAll.size(); i++) {
                UserFile tempUserFile = OnlineManager.GetUserData(userAll.get(i), 0L);
                if (tempUserFile != null) {
                    for (int j = 0; j < tempUserFile.player.size(); j++) {
                        File temp = tempUserFile.player.get(j);
                        if (temp.getName().equals(org)) {
                            temp.delete();
                        }
                    }
                    //WriteLog.writeOrgLog(tempUserFile,res.res,org,"OrgDelete"); //每个组织内的用户都需要记录日志
                    WriteLog.writeLog(tempUserFile, res.res,"OrgDelete",org);
                }
            } //删除组织中每个用户的组织文件
            OnlineManager.RemoveOrg(org);//从在线树中删除组织
            DatabaseManager.Remove(org); //删除组织文件
        }
        return res;
    }

}
