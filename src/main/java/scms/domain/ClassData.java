package scms.domain;
/***
 * @author zjs
 * @date 2023/3/6 11:55
 * @function 课程内容item
 */
public class ClassData {
    //    判断数据是否有效
    public boolean visible;
//    条目唯一标识id,目前很鸡肋
    public int id;
//    类型 集体活动 个人活动 课程
    public int type;
//    映射到hash表上的title
    public int title;
//    地点id
    public int location;
//  0代表单次结束,1代表一天为周期
    public int circle;
//    开始时间的int表示,存储的数据是当下的年份
    public int begin;
//    长度
    public int length;
}