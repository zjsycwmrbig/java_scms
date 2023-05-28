package scms.domain.ReturnJson;

/***
 * @author Administrator
 * @date 2023/5/27 14:32
 * @function 新的技巧类,可以灵活替换返回数据
 */
public class Return<Object> {
    public boolean res;
    public String state;
    public Object data;

    public Return(boolean res, String state, Object data) {
        this.res = res;
        this.state = state;
        this.data = data;
    }
}
