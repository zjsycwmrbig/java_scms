package scms.domain.ServerJson;

import java.util.Comparator;

/***
 * @author Administrator
 * @date 2023/4/24 12:08
 * @function
 */
public class compareQuery implements Comparator<QueryEventItem> {

    @Override
    public int compare(QueryEventItem o1, QueryEventItem o2) {
        if(o1.score > o2.score)return 1;
        else if(o1.score < o2.score) return -1;
        return 0;
    }
}
