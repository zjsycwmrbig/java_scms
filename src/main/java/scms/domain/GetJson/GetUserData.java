package scms.domain.GetJson;

import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
//定义一个实体类
public class GetUserData implements Serializable {

    private long username;
    private String password;
//    权限,管理员
    private String netname;

    private String personalword;

    public long getUsername() {
        return username;
    }

    public void setUsername(long username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNetname() {
        return netname;
    }

    public void setNetname(String netname) {
        this.netname = netname;
    }

    public String getPersonalword() {
        return personalword;
    }

    public void setPersonalword(String personalword) {
        this.personalword = personalword;
    }
}
