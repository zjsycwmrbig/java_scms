package scms.domain.ServerJson;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/31 18:47
 * @function
 */
public class EventDataByType {
    public String name;
    public int type;//类型 - 个人或者组织

    public List<EventItem> data; //数据

    public EventDataByType(String name, int type, List<EventItem> data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }
}
