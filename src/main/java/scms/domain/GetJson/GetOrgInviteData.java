package scms.domain.GetJson;

import java.io.Serializable;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/4/25 13:59
 * @function
 */
public class GetOrgInviteData implements Serializable {
    public String org;
    public List<Long> userlist;

    public String tips;
}
