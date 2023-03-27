package scms.Interceptor;

import org.apache.coyote.RequestInfo;
import org.springframework.stereotype.Component;
import scms.Dao.SCMSFILE;

/***
 * @author Administrator
 * @date 2023/3/27 22:01
 * @function
 */

@Component
public class BridgeData {
    private static ThreadLocal<SCMSFILE> requestInfoThreadLocal = new ThreadLocal<>();

    public static void setRequestInfo(SCMSFILE requestInfo) {
        requestInfoThreadLocal.set(requestInfo);
    }

    public static SCMSFILE getRequestInfo() {
        return requestInfoThreadLocal.get();
    }

    public static void clear() {
        requestInfoThreadLocal.remove();
    }
}
