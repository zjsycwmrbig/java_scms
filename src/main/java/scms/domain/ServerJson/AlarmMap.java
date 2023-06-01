package scms.domain.ServerJson;

import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/5/31 22:42
 * @function
 */
public class AlarmMap implements Serializable {
    int type;// 是owner或者player
    int index;// 是第几个文件

    public AlarmMap(int type, int index) {
        this.type = type;
        this.index = index;
    }
}
