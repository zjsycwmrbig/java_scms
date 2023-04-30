package scms.domain.GetMapJson;

import scms.Interceptor.BridgeData;
import scms.Service.OnlineManager;
import scms.domain.GetJson.GetOrgJionData;
import scms.domain.ServerJson.NoticeData;
import scms.domain.ServerJson.UserFile;

import java.io.*;
import java.util.Date;

/***
 * @author Administrator
 * @date 2023/4/25 13:26
 * @function 静态类,返回NoticeData文件,根据传入参数生成请求
 */
public class NoticeMaker {
    public static final int NOTICETIP = 0;// 0等偶数代表就一个知道了的通知

    public static final int INVITEJION = 1;// 1等奇数表示具有选择的通知,比如同意拒绝等


    //把请求数据序列化成字符串
    static public String GetDataJson(Object o){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            byte[] bytes = bos.toByteArray();
            return new String(bytes);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //把字符串反序列化成对象
    static public Object GetDataObj(String string) {
        try {
            byte[] bytes = string.getBytes();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //得到JoinOrg加入组织的通知
    //指定提示语
    static public NoticeData JoinOrg(String tips,String org){
        //返回noticeData
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);//1代表不是临时数据
        return new NoticeData((new Date()).getTime(),user.username,user.netname,INVITEJION,tips,GetDataJson(new GetOrgJionData(org)));
    }
    //默认提示语 xx提醒你加入xx
    static public NoticeData JoinOrg(String org){
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        return new NoticeData((new Date()).getTime(),user.username,user.netname,INVITEJION,user.netname +  "邀请您加入" + org,GetDataJson(new GetOrgJionData(org)));
    }
    //直接是通知,没有其他的东西
    static public NoticeData NoticeTip(String tips){
        UserFile user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        return new NoticeData((new Date()).getTime(),user.username,user.netname,NOTICETIP,tips,null);
    }
}
