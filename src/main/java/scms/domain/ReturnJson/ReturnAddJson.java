package scms.domain.ReturnJson;

import scms.domain.ServerJson.ClashData;

import java.util.List;

/***
 * @author Administrator
 * @date 2023/4/7 16:47
 * @function
 */
public class ReturnAddJson extends ReturnJson{
    public List<ClashData> clashList;
    List<Long> adviceBeginTime;//建议的时间
    List<Long> adviceEndTime;//建议的结束时间
    public ReturnAddJson(boolean res, String state) {
        super(res, state);
    }
}
