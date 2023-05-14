package scms.domain.GetJson;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/***
 * @author zjs
 * @date 2023/3/6 11:55
 * @function 课程内容item
 */
@Component
public class ClassData implements Serializable {
    //指明是哪个组织的数据
    public String group;
    //数据和用户之间的关系
    public int type;
//    映射到hash表上的title
//    更改:因为class分开存储,title重复减少,直接存储在这里面
    public String title;
//    地点id
    public int location;
//  0代表单次结束,1代表一天为周期
    public int circle;
//    开始时间的int表示,存储的数据是当下的年份
    public long begin;
    public long end;
//    活动/课程时间长度
    public long length;
}
