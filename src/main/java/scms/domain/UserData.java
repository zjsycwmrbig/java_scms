package scms.domain;

import org.springframework.stereotype.Component;

@Component
//定义一个实体类
public class UserData {

    private String username;
    private String password;
    private String className;
//    权限,管理员
    private int right;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
//    加一个班级
}
