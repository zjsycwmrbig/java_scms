package scms.domain.ServerJson;
import scms.Service.DataManager;

import java.util.Date;

/***
 * @author Administrator
 * @date 2023/4/28 15:56
 * @function 用户信息的维护
 */
public class OnlineData {
    public Date lastLogin;//上次登录时间
    public UserFile user;//包含用户信息等数据
    public DataManager data;//包含用户的数据
}
