package scms.domain.ServerJson;

import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/4/25 13:12
 * @function 通知消息,返回到前端用户
 */
public class NoticeData implements Serializable{
    public NoticeData(long index, long from, String name, int noticeId, String noticeTip, String requestData) {
        this.index = index;
        this.from = from;
        this.name = name;
        this.noticeId = noticeId;
        this.noticeTip = noticeTip;
        this.requestData = requestData;
    }

    public long index;//表明序列号,方便后面删除
    public long from;//来自那个人,可以对那个人进行通知

    public String name;//发起人姓名
    public int noticeId;//通知id号,对应着请求到底要干什么,同时也可以标识前端的通知是什么
    public String noticeTip;//通知提示,一句话
    public String requestData;//请求数据,通常序列化和反序列化实现

}
