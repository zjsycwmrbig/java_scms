package scms.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import scms.Dao.DataProcessor;
import scms.Dao.DatabaseManager;
import scms.Interceptor.BridgeData;
import scms.Dao.WriteLog;
import scms.Service.DataManager;
import scms.Service.FuncManager;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.GetJson.GetOrgInviteData;
import scms.domain.GetMapJson.NoticeMaker;
import scms.domain.ReturnJson.Return;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.util.List;
import java.util.Objects;

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
    //接收org名称,默认生成一个口令
    // -1.org已经存在
    // -2.捕获异常
    @RequestMapping("create")
    public Return<String> OrgCreate(@RequestParam("org") String org, @RequestParam("password") boolean password){
        File file = DatabaseManager.AddItem(org); //新建一个组织文件
        if (file == null){
            return new Return<>(false,"组织已存在","");
        }

        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);//用户添加组织信息
        user.owner.add(file); //添加组织信息
        DataProcessor dataProcessor = new DataProcessor(BridgeData.getRequestInfo(),org,file);

        dataProcessor.dataItem.type = user.owner.size()-1;
        // password
        if(password){
            dataProcessor.dataItem.password = FuncManager.GenerateOrgPassword();
        }else{
            dataProcessor.dataItem.password = "";
        }
        OnlineManager.NewOnlineData(dataProcessor,1L);//直接新建一个
        WriteLog.writeLog(user,true,"OrgCreate",org);
        return  new Return<>(true,"组织创建成功!"+(password?"口令为"+dataProcessor.dataItem.password:""),dataProcessor.dataItem.password);
    }

    //接收org名称
    // -1. 组织不存在
    // -2. 组织中存在冲突
    @RequestMapping("/join")
    public ReturnJson OrgJoin(@RequestParam("org") String org, @RequestParam("password") String password){
        //加入某个组织,默认已经通过拦截
        ReturnJson res =  new ReturnAddJson(true,"加入成功");
        DataManager dataManager = new DataManager();
        DataProcessor Org = OnlineManager.GetEventData(org,0L);
        if(Org == null){
            res.res = false;res.state = "组织不存在";
        }else{
            //判断该组织是否为私人组织
            if(Org.dataItem.type == 0){
                return new ReturnJson(false,"该组织为私人组织");
            }else{
                // 判断是否已经加入
                if(Org.dataItem.users.contains(BridgeData.getRequestInfo())){
                    return new ReturnJson(false,"已经加入该组织");
                }
                //判断口令是否正确
                System.out.println("=============================");
                System.out.println("判断口令是否正确");
                System.out.println("口令为:"+Org.dataItem.password);
                System.out.println("输入口令为:"+password);
                if(!Objects.equals(Org.dataItem.password, password) && !Org.dataItem.password.equals("")){
                    return new ReturnJson(false,"口令错误");
                }
                System.out.println("=============================");
                System.out.println("口令正确");
                res = dataManager.AddOrg(Org); //先获得组织冲突文件,如果成功在这里就添加到用户和组织信息里面做双向连接
            }
            if(res.res){
                OnlineManager.GetEventData(org,1L);//给这份文件添加权重
            }
        }
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        WriteLog.writeLog(userFile, res.res, "OrgJoin",org);
        return res;
    }

//    @RequestMapping("/delete")
//    public ReturnJson OrgDelete(@RequestParam String org){
//        //删除某个组织
//        ReturnJson res = new ReturnJson(true,"组织删除成功");
//        DataProcessor Org = OnlineManager.GetEventData(org,0L);
//        List<Long> userAll = null;
//        if(Org == null) {
//            res.res = false;
//            res.state = "组织不存在";
//        }
//        else {
//            userAll = Org.dataItem.users; //得到组织中所有用户的信息
//            for (int i = 0; i < userAll.size(); i++) {
//                UserFile tempUserFile = OnlineManager.GetUserData(userAll.get(i), 0L);
//                if (tempUserFile != null) {
//                    for (int j = 0; j < tempUserFile.player.size(); j++) {
//                        File temp = tempUserFile.player.get(j);
//                        if (temp.getName().equals(org)) {
//                            temp.delete();
//                        }
//                    }
//                    //WriteLog.writeOrgLog(tempUserFile,res.res,org,"OrgDelete"); //每个组织内的用户都需要记录日志
//                    WriteLog.writeLog(tempUserFile, res.res,"OrgDelete",org);
//                }
//            } //删除组织中每个用户的组织文件
//            OnlineManager.RemoveOrg(org);//从在线树中删除组织
//            DatabaseManager.Remove(org); //删除组织文件
//        }
//        return res;
//    }
    // 删除组织
    @RequestMapping("/delete")
    public Return<Object> DeleteOrg(@RequestParam("org") String org){
        DataManager dataManager = new DataManager();
        return dataManager.deleteOrg(org);
    }

    // 退出组织
    @RequestMapping("/quit")
    public Return<Object> OrgQuit(@RequestParam String org){
        //退出某个组织,需要处理userFile,dataProcessor,闹钟信息,如果是组织创建者,则需要删除组织,如果是组织成员,则需要删除组织中的用户信息
        DataManager dataManager = new DataManager();
        return dataManager.removeOrg(BridgeData.getRequestInfo(),org);
    }
    // 删除组织成员
    @RequestMapping("/removeMember")
    public Return<Object> removeMember(@RequestParam("org") String org,@RequestParam("member") long member){
        DataManager dataManager = new DataManager();
        return dataManager.removeOrgMember(org,member);
    }

    @RequestMapping("/changePassword")
    public Return<Object> changePassword(@RequestParam("org") String org,@RequestParam("password") String password){
        DataManager dataManager = new DataManager();
        return dataManager.changeOrgPassword(org,password);
    }


}
