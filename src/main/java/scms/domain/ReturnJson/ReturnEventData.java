package scms.domain.ReturnJson;

import scms.domain.ServerJson.EventDataByTime;
import scms.domain.ServerJson.EventDataByType;

import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/31 18:44
 * @function
 */
public class ReturnEventData extends ReturnJson{
    public ReturnEventData() {
        super(true,"");
        this.events = new ArrayList<>();this.routines = new ArrayList<>();
    }
    public int total;
    //    返回事件数据
    public ArrayList<EventDataByType> events;//数据列表,按照页面分配,这个的意义

    public ArrayList<EventDataByTime> routines;//按照时间分配所以是routine


}
