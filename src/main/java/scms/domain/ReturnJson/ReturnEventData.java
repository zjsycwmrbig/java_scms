package scms.domain.ReturnJson;

import scms.domain.ServerJson.EventData;

import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/31 18:44
 * @function
 */
public class ReturnEventData extends ReturnJson{
    public ReturnEventData() {
        this.events = new ArrayList<>();
    }

    //    返回事件数据
    public ArrayList<EventData> events;//数据列表
}
