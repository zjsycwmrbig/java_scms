package scms.Interceptor;

import java.io.Serializable;

/**
 * @author seaside
 * 2023-05-25 21:45
 */
public class Log implements Serializable {
    String localDateTime; //传的是格式化后的时间，所以是String
    long userName;
    String operate;

    public Log(String localDateTime, long userName, String operate) {
        this.localDateTime = localDateTime;
        this.userName = userName;
        this.operate = operate;
    }
}
