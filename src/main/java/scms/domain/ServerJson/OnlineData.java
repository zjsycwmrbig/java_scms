package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/4/28 15:56
 * @function 用户信息的维护
 */
public class OnlineData<T> {
    public Long cache;//上次登录时间
    public T data;//用户数据或者DataProcessor
}
