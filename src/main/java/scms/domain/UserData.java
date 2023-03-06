package scms.domain;

import org.springframework.stereotype.Component;

@Component
//定义一个实体类
public class UserData {
    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String username;
    public String password;
//    权限
    public int right;
}
