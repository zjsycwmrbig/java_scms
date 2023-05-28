package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/4/7 19:43
 * @function
 */
public class ClashItem {
    public String group;//冲突的组织名称
    public long time;//冲突起始点
    public int type;//事项类型
    public long clashDeg;//冲突程度
    public String title;//冲突类型

    public ClashItem(String group, long time, int type, long clashDeg, String title) {
        this.group = group;
        this.time = time;
        this.type = type;
        this.clashDeg = clashDeg;
        this.title = title;
    }
}
