package scms.Interceptor;

import org.springframework.stereotype.Component;
import scms.domain.ServerJson.UserFile;

/***
 * @author Administrator
 * @date 2023/3/27 22:01
 * @function
 */

@Component
public class BridgeData {
    private static ThreadLocal<UserFile> requestInfoThreadLocal = new ThreadLocal<>();

    public static void setRequestInfo(UserFile requestInfo) {

        requestInfoThreadLocal.set(requestInfo);
    }

    public static UserFile getRequestInfo() {
        return requestInfoThreadLocal.get();
    }

    public static void clear() {
        requestInfoThreadLocal.remove();
    }
}
