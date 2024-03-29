package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/3/28 19:59
 * @function
 */

//json 返回值 能填充的填充
public class EventItem implements Comparable<EventItem>{
    public EventItem(int type, String title, String location, long begin, long length,String locationData,int indexID,String group){
        this.type = type;
        this.title = title;
        this.begin = begin;
        this.length = length;
        this.location = location;
        this.locationData = locationData;
        this.indexID = indexID;
        this.group = group;
    }
    public int type;//给出类型
    public String group;//组织信息
    public String title;
    public String location;//不如填充成字符串
    public long begin;
    public long length;
    public String locationData;//补充的地点信息，线上信息
    public int indexID;// 数据页码,同时也是事件的owner和player标记

    public boolean alarmFlag; // 闹钟标记
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
