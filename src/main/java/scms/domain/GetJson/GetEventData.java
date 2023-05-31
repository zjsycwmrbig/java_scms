package scms.domain.GetJson;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/***
 * @author zjs
 * @date 2023/3/6 11:55
 * @function 课程内容item
 */
@Component
public class GetEventData implements Serializable {
    //指明是哪个组织的数据
    public String group;
    //数据和用户之间的关系,0代表个人事件，1则代表拥有组织的序号
    public int indexID;
//    映射到hash表上的title
//    title可以用空格隔开,可以有多个title,主要针对临时事件
    public String title;
//    地点id 用空格隔开,可以有多个地点
    public String location;
//  0代表单次结束,1代表一天为周期,之后的数字代表这个事件的次数
    public int size;
//    开始时间的int表示,存储的数据是当下的年份
    public long begin;
    public String locationData;//补充的地点信息，线上信息
    public long end;
//    活动/课程时间长度
    public long length;
//    课程、活动、临时 0 1 2
    public int type;
//    课程的周期
    public int circle;
    // 记录闹钟,也可以看做事项的重要性
    public boolean alarmFlag;
}
