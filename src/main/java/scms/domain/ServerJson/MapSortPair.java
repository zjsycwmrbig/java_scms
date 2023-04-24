package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/4/24 10:42
 * @function 工具类,真的烦,必须不停新建类
 */

public class MapSortPair{
    public MapSortPair(Long id,int score, int multiple) {
        this.id = id;
        this.score = score;
        this.multiple = multiple;
    }
    public Long id;//事项id
    public int score;//分数
    public int multiple;//激励倍数

    public int compareTo(MapSortPair o) {
        if(this.score > o.score) return 1;
        else return -1;
    }
}
