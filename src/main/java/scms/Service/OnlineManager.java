package scms.Service;

import scms.Dao.DataProcessor;
import scms.Dao.RBTree;
import scms.Interceptor.FileManager;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.OnlineData;
import scms.domain.ServerJson.RBTNode;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Stack;

/***
 * @author Administrator
 * @date 2023/4/28 15:50
 * @function 作为一个在线的维护系统,维护一棵数据树,把在线的用户数据存储起来,所有的数据访存都需要先访问这棵树再访问内存
 * 1. 登录时加入在线树
 * 2. 登出时销毁在线树
 * 3. 访问时先访问在线树 -修改用户信息 -修改课程信息 -修改组织信息
 * 4. 唯一需要绕开在线树通知的是添加通知给某些用户
 */

public class OnlineManager {
    final static long LeaveTime = 10*10*1000;//设置多久一个用户才算离去,这里设置100s
    static RBTree<OnlineData,Long> onlineTree = new RBTree<>();
    static Stack<RBTNode> LeaveUser = new Stack<>();//离去的用户
    //返回在在线树里面找到的数据
    static OnlineData GetData(Long user){
        return PiggySearch(user);
    }
    // 特殊的search
    static OnlineData PiggySearch(Long user){
        return PiggySearch(onlineTree.Root,user);
    }

    static OnlineData PiggySearch(RBTNode x,Long key){
        if (x==null) return null;
        int cmp = (key).compareTo((Long) x.key);
        if(cmp != 0){
            if((new Date()).getTime()-((OnlineData)(x.vaule)).lastLogin.getTime()> LeaveTime){
                LeaveUser.push(x);//把x加入LeaveUser的栈里面,定时清理这个离开的栈
            }
        }
        if (cmp < 0) {
            return PiggySearch(x.left, key);
        }
        else if (cmp > 0) {
            return PiggySearch(x.right, key);
        }
        else {
            ((OnlineData)(x.vaule)).lastLogin = (new Date());//赋值为最新的
            return (OnlineData) (x.vaule);
        }
    }

    //添加用户到在线树
    static private void AddOnline(OnlineData data){
        onlineTree.insert(data,data.user.username);
    }

    static public void AddOnline(UserFile user){
        OnlineData onlineData = new OnlineData();
        onlineData.user = user;
        onlineData.data = new DataManager(user);//创建一个DataManager
        onlineData.lastLogin = new Date();
        AddOnline(onlineData);
    }

    //删除用户树,指定一个清除
    static public ReturnJson RemoveOnline(Long user){
        ReturnJson returnJson = new ReturnJson(true,"登出成功");
        RBTNode node = onlineTree.searchNode(onlineTree.Root,user);
        if(node != null) {
            try {
                ((OnlineData) node.vaule).data.owner.get(0).print();
                SaveFile((OnlineData) node.vaule);//这个地方保存失败！！！

                System.out.println("进行移除");

                onlineTree.remove(node);
                System.out.println(PiggySearch(user));
            }catch (Exception e){
                returnJson.res = false;
                returnJson.state = "服务器出错";
            }
        }else{
            returnJson.res =false;
            returnJson.state = "用户未登录";
        }
        return returnJson;
    }
    //清除Leave用户栈
    static public void ClearLeave(){
        while(!LeaveUser.empty()){//栈非空,一个个清除数据
            onlineTree.remove(LeaveUser.pop());
        }
    }

    //保存用户数据
    static public boolean SaveFile(OnlineData data){
        //存储用户信息
        try {
            FileOutputStream fileOut = new FileOutputStream(UserManager.GetUserFile(data.user.file));//session中存储用户username和file地址!!!
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data.user);
            out.close();
            fileOut.close();
        }catch (Exception e){
            return false;
        }
        //存储数据信息
        return SaveData(data.user, data.data);
    }
    public static boolean SaveData(UserFile user, DataManager data){
        if(user.owner != null && user.owner.size() != 0){
            for(int i = 0;i < user.owner.size();i++){
                if(!SaveDataItem(data.owner.get(i),user.owner.get(i))) return false;//一个个保存
            }
        }

        if(user.player != null && user.player.size() != 0){
            for(int i = 0 ;i < user.player.size();i++){
                if(!SaveDataItem(data.player.get(i),user.player.get(i)))return false;//一个个保存
            }
        }
        return true;
    }
    //依次存储DataProcess
    private static boolean SaveDataItem(DataProcessor data, File file){
        File tempFile = FileManager.NextFile(file,"DataItem");
        System.out.println("保存到"+tempFile.getAbsolutePath());
        try {
            FileOutputStream fileOut1 = new FileOutputStream(tempFile);
            ObjectOutputStream out1 = new ObjectOutputStream(fileOut1);
            out1.writeObject(data.dataItem);
            out1.close();
            fileOut1.close();
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }

        tempFile = FileManager.NextFile(file,"DataMap");
        try {
            FileOutputStream fileOut2 = new FileOutputStream(tempFile);
            ObjectOutputStream out2 = new ObjectOutputStream(fileOut2);
            out2.writeObject(data.dataMap);
            out2.close();
            fileOut2.close();
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }

        tempFile = FileManager.NextFile(file,"DataRBTree");
        try {
            FileOutputStream fileOut3 = new FileOutputStream(tempFile);
            ObjectOutputStream out3 = new ObjectOutputStream(fileOut3);
            out3.writeObject(data.dataRBTree);
            out3.close();
            fileOut3.close();
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }

        return true;
    }
}
