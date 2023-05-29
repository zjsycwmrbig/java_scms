package scms.domain.ReturnJson;

import org.springframework.context.annotation.Bean;
import scms.domain.ServerJson.Log;

import java.io.Serializable;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/5/28 23:33
 * @function
 */
public class ReturnLogData extends ReturnJson implements Serializable {
    public int total;
    public List<Log> logs;

    public ReturnLogData(boolean res, String state, int total, List<Log> logs) {
        super(res, state);
        this.total = total;
        this.logs = logs;
    }
}
