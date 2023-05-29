package scms.domain.ReturnJson;

import scms.domain.ServerJson.NoticeData;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/30 10:24
 * @function
 */
public class ReturnUserData extends ReturnJson{
    public ReturnUserData(String netname, String personalWord,String state,boolean hasImage) {
        super(true,state);
        this.netname = netname;
        this.PersonalWord = personalWord;
        this.hasImage = hasImage;
    }
    public long username;
    public String netname;

    public String PersonalWord;

    public List<String> owner;
    public List<String> player;
    public List<NoticeData> tips;

    public List<List<Long>> dataUser;

    public boolean hasImage;

}
