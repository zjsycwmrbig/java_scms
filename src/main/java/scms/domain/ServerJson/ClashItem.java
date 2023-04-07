package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/4/7 19:43
 * @function
 */
public class ClashItem {
    public String name;//冲突的组织名称
    public long time;//冲突起始点
    public long id;//冲突数据id

    public long clashDeg;//冲突程度

    public ClashItem(String name, long time, long id, long clashDeg) {
        this.name = name;
        this.time = time;
        this.id = id;
        this.clashDeg = clashDeg;
    }
}
