package scms.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import scms.Interceptor.BridgeData;
import scms.Service.OnlineManager;
import scms.domain.GetJson.GetOrgJionData;
import scms.domain.GetMapJson.NoticeMaker;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.OnlineData;
import scms.domain.ServerJson.UserFile;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/4/25 23:23
 * @function
 */
@Controller
@ResponseBody
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private OrgController orgController;
    //处理通知
    @RequestMapping("/yes")
    public ReturnJson NoticeProcessor(@RequestBody NoticeData notice){
        ReturnJson returnJson = null;
        //处理
        switch (notice.noticeId){
            case (NoticeMaker.NOTICETIP):{
                //直接发送消息通知到用户,提醒下的不用处理
                break;
            }
            case (NoticeMaker.INVITEJION) :{
                //根据通知的消息选择合适的Service进行处理
                returnJson = orgController.OrgJion((GetOrgJionData) NoticeMaker.GetDataObj(notice.requestData));
                if(returnJson.res){
                    //成功,发送通知,删除通知


                }else{
                    //失败,表示错误
                    returnJson = new ReturnJson(false,"加入组织出错");
                }
                break;
            }
            default:{
                returnJson = new ReturnJson(false,"没有找到相关通知");
            }
        }
        return returnJson;
    }
    //抛弃通知
    @RequestMapping("/no")
    public ReturnJson NoticeRemover(@RequestBody NoticeData notice){
        // 这里需不需要再要求通知一下发送人,应该是需要
        return new ReturnJson(true,"");
    }

    //轮询获取通知
    // 这里起始有个问题,怎么确定通知里面是不是新的,可以通过通知的有序添加完成
    @RequestMapping("/Polling")
    public List<NoticeData> NoticePolling(){
        //只能从在线树中获得,并且必然不是null
        OnlineData onlineData = OnlineManager.PiggySearch(BridgeData.getRequestInfo().username);
        return ((UserFile)(onlineData.data)).notice;//返回数据
    }
}
