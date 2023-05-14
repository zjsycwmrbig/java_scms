package scms.domain.ServerJson;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/***
 * @author zjs
 * @date 2023/3/29 23:51
 * @function 更新后的用户信息,不再只是静态信息,而是可以通过FILE操作指定的文件
 */

public class UserFile implements Serializable {
    public UserFile(long username, String netname, String password, String personalWord) {
        this.username = username;
        this.netname = netname;
        this.password = password;
        this.PersonalWord = personalWord;
        this.notice = new ArrayList<>();//创建对象
        this.hasImage = false;
    }

    public long username;//账号
    public String netname;//姓名
    public String password;//密码
//  个性签名
    public String PersonalWord;//签名

//  文件组,分为拥有者,享有者
    public List<File> owner; //拥有者,管理员,读写权限

    public List<File> player;//使用者,只读权限

    public List<NoticeData> notice; //通知

    public File file;//文件位置

    public boolean hasImage;//是否有头像
}
