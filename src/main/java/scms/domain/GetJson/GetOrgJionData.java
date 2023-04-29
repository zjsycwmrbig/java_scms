package scms.domain.GetJson;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/4/26 10:07
 * @function
 */
public class GetOrgJionData implements Serializable {
    public GetOrgJionData(String org) {
        this.org = org;
    }
    public String org;//组织名
}
