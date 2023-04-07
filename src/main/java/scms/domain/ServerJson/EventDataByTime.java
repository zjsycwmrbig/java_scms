package scms.domain.ServerJson;

import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/4/2 17:13
 * @function 处理分类问题,实现按照weekIndex分页的想法,方便前端处理
 */

public class EventDataByTime{
    public EventDataByTime(int weekIndex) {
        this.weekIndex = weekIndex;
        this.list = new ArrayList<>();
    }

    //    按照天来分布
    int weekIndex;

    public List<EventItem> list;
}
