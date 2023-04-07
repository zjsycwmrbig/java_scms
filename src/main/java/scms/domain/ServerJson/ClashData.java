package scms.domain.ServerJson;

import scms.domain.GetJson.ClassData;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/4/7 16:48
 * @function
 */
public class ClashData {
    public ClashData() {
        this.clashNum = 0;
    }

    public int type; //是否是本人冲突
    public String netName;//和谁有冲突
    public int clashNum; //冲突数量

    public List<ClashItem> list;//冲突事件列表,这里后面应该还会有排序之类的
}
