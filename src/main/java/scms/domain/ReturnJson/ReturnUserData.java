package scms.domain.ReturnJson;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/30 10:24
 * @function
 */
public class ReturnUserData extends ReturnJson{
    public ReturnUserData(String netname, String personalWord,String state) {
        this.netname = netname;
        PersonalWord = personalWord;
        super.state = state;
    }
    public String netname;

    public String PersonalWord;

    public List<String> owner;
    public List<String> player;

}
