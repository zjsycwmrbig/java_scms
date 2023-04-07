package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/3/28 19:59
 * @function
 */

//json 返回值

public class EventItem implements Comparable<EventItem>{
    public EventItem(int type, String title, int location, long begin, long length){
        this.type = type;
        this.title = title;
        this.begin = begin;
        this.length = length;
        this.location = location;
    }
    public int type;//给出类型
    public String title;
    public int location;
    public long begin;
    public long length;

    @Override
    public int compareTo(EventItem o) {
        if(this.begin < o.begin){
            return -1;
        }else if(this.begin > o.begin){
            return 1;
        }else{
            return 0;
        }
    }
}
