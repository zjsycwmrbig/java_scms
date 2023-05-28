package scms.domain.ServerJson;

import scms.domain.GetJson.GetEventData;

/***
 * @author Administrator
 * @date 2023/4/24 11:54
 * @function 返回的数据
 */
public class QueryEventItem {
    public QueryEventItem(GetEventData item, int score) {
        this.item = item;
        this.score = score;
    }

    public GetEventData item;
    public int score;
}
