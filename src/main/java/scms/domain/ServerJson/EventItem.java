package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/3/28 19:59
 * @function
 */

//json 返回值

public class EventItem {
    public EventItem(int id, String title, int location, long begin, long length){
        this.id = id;
        this.title = title;
        this.begin = begin;
        this.length = length;
        this.location = location;
    }
    public int id;
    public String title;
    public int location;
    public long begin;
    public long length;
}
