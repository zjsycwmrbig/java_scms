package scms.domain.ServerJson;

import scms.domain.GetJson.ClassData;

/***
 * @author zjs
 * @date 2023/3/13 11:07
 * @function
 */
public class ClashErrorData extends ErrorData{
    public ClashErrorData() {
        super.type = "冲突错误";
        super.state = true;
    }

    public int circle;//多少个周期后出现错误
    public ClassData item;//和哪一个事件出现冲突

}
