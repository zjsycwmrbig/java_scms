package scms.domain;

/***
 * @author Administrator
 * @date 2023/3/28 19:59
 * @function
 */

//json 返回值

public class ItemData {
    public ItemData(int id, String title, int type, int location, long begin, long length){
        this.id = id;
        this.title = title;
        this.type = type;
        this.begin = begin;
        this.length = length;
    }
    public int id;
    public String title;
    public int type;
    public int location;
    public long begin;
    public long length;
}
