package scms.domain;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/***
 * @author zjs
 * @date 2023/3/29 23:51
 * @function 更新后的用户信息,不再只是静态信息,而是可以通过FILE操作指定的文件
 */

public class UserFile implements Serializable {
    public long username;
    public String password;
//  个性签名
    public String PersonalWord;

//  文件组,分为拥有者,享有者
    public List<File> owner;

    public List<File> user;

}
