package scms.domain.ServerJson;

import scms.Dao.RBTree;
import scms.Dao.WriteLog;
import scms.domain.GetJson.GetEventData;

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

    public File file;//文件位置

    private RBTree<AlarmMap,Long> alarmTree = new RBTree<>();

    public boolean hasImage;//是否有头像

    // 仅仅标记某个节点是有闹钟的,这里默认返回的是按照indexID正负
    public boolean AddAlarm(long key,int indexID){
        RBTNode<AlarmMap,Long> node = alarmTree.searchNode(alarmTree.Root,key);
        boolean isOk;
        if(node != null){
            // 已经存在
            isOk = false;
        }else{
            alarmTree.insert(new AlarmMap(indexID >=0 ? 0 : 1,indexID >= 0 ? indexID : -indexID - 1),key);
            isOk = true;
        }
        WriteLog.writeLog(this,isOk,"AddAlarm","");
        return isOk;
    }

    public boolean DeleteAlarm(long key){
        RBTNode<AlarmMap,Long> node = alarmTree.searchNode(alarmTree.Root,key);
        boolean isOK;
        if(node == null){
            // 已经存在
            isOK = false;
        }else{
            alarmTree.remove(node);
            isOK = true;
        }
        WriteLog.writeLog(this,isOK,"DeleteAlarm","");
        return isOK;
    }

    public AlarmMap SearchAlarm(long key){
        RBTNode<AlarmMap,Long> node = alarmTree.searchNode(alarmTree.Root,key);
        if(node == null){
            // 没有这个节点 , 就返回null
            return null;
        }else{
            // 存在这个节点
            return node.vaule;
        }
    }
    public boolean Exist(long key){
        if(alarmTree.searchNode(alarmTree.Root,key) == null) {
            return false;
        }else{
            return true;
        }
    }

    public void AddAlarm(GetEventData item){
        // 依次加入到闹钟里面去
        for(long begin = item.begin;begin + item.length <= item.end;begin += item.circle * 24 * 60 *60 * 1000L){
            AddAlarm(begin,item.indexID);
            if(item.circle == 0) break;
        }
    }

}
