package scms.Service;

import scms.Dao.*;
import scms.Interceptor.FileManager;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.OnlineData;
import scms.domain.ServerJson.RBTNode;
import scms.domain.ServerJson.UserFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

/***
 * @author Administrator
 * @date 2023/4/28 15:50
 * @function 作为一个在线的维护系统,维护一棵数据树,把在线的用户数据存储起来,所有的数据访存都需要先访问这棵树再访问内存
 * 1. 登录时加入在线树
 * 2. 登出时销毁在线树
 * 3. 访问时先访问在线树 -修改用户信息 -修改课程信息 -修改组织信息
 * 4. 唯一需要绕开在线树通知的是添加通知给某些用户
 * 5. 多用户文件也需要创建一个在线数据树,这个也需要重新新建一个
 */

public class OnlineManager {

    //设置多久一个用户和一份数据多久才算离去,这里设置100s
    final static long LeaveTime = 10*10*1000;
    static RBTree<OnlineData<UserFile>,Long> onlineUser = new RBTree<>();//在线用户cache
    static RBTree<OnlineData<DataProcessor>,String> onlineData = new RBTree<>();//在线数据cache
    static Stack<RBTNode> Leave = new Stack<>();//离去的用户和数据

    //返回在在线树里面找到的用户数据,找不到就返回在系统中找到的数据,最后是null代表没有
    static public UserFile GetUserData(Long user,Long cache){
        OnlineData<UserFile> res = PiggySearch(user);
        if(res != null) return res.data;
        else {
            //没有 必须从文件里读取
            UserFile userFile = ReadUserFile(UserRBTree.searchFile(user));
            AddOnlineUser(userFile,cache);
            return userFile;
        }
    }

    //返回在online里面找到的Processor
    static public DataProcessor GetEventData(String org,Long cache){
        OnlineData<DataProcessor> res = PiggySearch(org);
        if(res != null) return res.data;
        else {
            //通过这个函数调用的
            DataProcessor dataProcessor = ReadDataProcessor(DatabaseRBTree.searchFile(org));
            AddOnlineData(dataProcessor,cache);
            return dataProcessor;
        }
    }

    // 特殊的search
    static public OnlineData PiggySearch(Long user){
        RBTNode node = PiggySearch(onlineUser.Root,user);
        if(node != null) return (OnlineData) (node.vaule);
        else return null;
    }

    static public OnlineData PiggySearch(String org){
        RBTNode node = PiggySearch(onlineData.Root,org);
        if(node != null) return (OnlineData) (node.vaule);
        else return null;
    }
    //泛型寻找
    static RBTNode PiggySearch(RBTNode x,Object key){
        if (x==null) return null;
        int cmp;
        if(key instanceof Long) cmp = ((Long)(key)).compareTo((Long) x.key);
        else cmp = ((String)(key)).compareTo((String) x.key);

        if(cmp != 0){
            if(IsDeadNode(x)){
                Leave.push(x);//把x加入LeaveUser的栈里面,定时清理这个离开的栈
            }
        }
        if (cmp < 0) {
            return PiggySearch(x.left, key);
        }
        else if (cmp > 0) {
            return PiggySearch(x.right, key);
        }
        else {
            if(((OnlineData)(x.vaule)).data instanceof UserFile) ((OnlineData)(x.vaule)).cache = (new Date()).getTime();//如果是用户的话赋值为最新的
            return  x;
        }
    }

    static private void AddOnline(OnlineData data){
        if(data == null) return;
        if(data.data instanceof UserFile) onlineUser.insert(data,((UserFile) data.data).username);
        else onlineData.insert(data,((DataProcessor)(data.data)).name);
    }

    //添加用户或者数据到在线树
    // ---重点是不能添加重复!!!

    //添加一整套数据到在线树,一般是
    static public List<List<Long>> AddOnlineDataList(UserFile user){
        List<List<Long>> list = new ArrayList<>();
        //只是把所有的datalist加入在线树
        if(user.owner != null && user.owner.size() != 0){
            for(int i = 0;i < user.owner.size();i++){
                DataProcessor dataProcessor = ReadDataProcessor(user.owner.get(i));
                list.add(dataProcessor.dataItem.users);
                AddOnlineData(dataProcessor,1L);
            }
        }

        if(user.player != null &&user.player.size() != 0){
            for(int i = 0 ;i < user.player.size();i++){
                AddOnlineData(ReadDataProcessor(user.player.get(i)),1L);
            }
        }
        //一个一个的加入,主要在登录的时候使用这个
        return list;
    }

    static public void AddOnlineUser(UserFile user,Long cache){
        if(PiggySearch(user.username)==null){//如果在线树里面没有给用户再添加
            OnlineData onlineData = new OnlineData();
            onlineData.data = user;
            if(cache == 0){
                onlineData.cache = 0L;
            }else{
                onlineData.cache = (new Date()).getTime();
            }
            AddOnline(onlineData); //添加用户在线信息
        }
    }
    // 向cache中添加已经存在的数据
    static  public void AddOnlineData(DataProcessor data,Long cache){
        RBTNode node = PiggySearch(onlineData.Root,data.name);
        if(node == null){
            OnlineData onlineData = new OnlineData();
            onlineData.data = data;
            onlineData.cache = cache;
            AddOnline(onlineData); //添加数据cache信息
        }else{
            //不是空,那就是需要加一
            ((OnlineData)(node.vaule)).cache += cache;//加1
        }
    }

    // 向cache中添加不存在的数据
    static public void NewOnlineData(DataProcessor data,Long cache){
        OnlineData onlineData = new OnlineData();
        onlineData.data = data;
        onlineData.cache = cache;
        AddOnline(onlineData);//添加数据cache信息
    }


    //删除用户树,指定一个清除,捎带删除相关的组织数据
    //根据username移除节点
    static public ReturnJson RemoveOnline(Long username){
        ReturnJson returnJson = new ReturnJson(true,"登出成功");

        RBTNode node = onlineUser.searchNode(onlineUser.Root,username);//查询用户节点

        if(node != null) {
            try {
                SaveUser((UserFile) (((OnlineData) (node.vaule)).data));
                onlineUser.remove(node);
            }catch (Exception e){
                returnJson.res = false;
                returnJson.state = "服务器出错";
                return returnJson;
            }
        }else{
            returnJson.res =false;
            returnJson.state = "用户未登录";
            return returnJson;
        }
        //移除data
        UserFile user = (UserFile) (((OnlineData) (node.vaule)).data);

        if(user.owner != null && user.owner.size() != 0){
            for(int i = 0;i < user.owner.size();i++){
                node = onlineData.searchNode(onlineData.Root,user.owner.get(i).getName());//找到节点
                ((OnlineData)(node.vaule)).cache --;
                if(IsDeadNode(node)){
                    SaveDataProcessor((DataProcessor) (((OnlineData) (node.vaule)).data),user.owner.get(i));//保存起来
                    onlineData.remove(node);
                }
            }
        }

        if(user.player != null && user.player.size() != 0){
            for(int i = 0 ;i < user.player.size();i++){
                node = onlineData.searchNode(onlineData.Root,user.player.get(i).getName());//找到节点
                ((OnlineData)(node.vaule)).cache --;
                if(IsDeadNode(node)){
                    SaveDataProcessor((DataProcessor) (((OnlineData) (node.vaule)).data),user.player.get(i));//保存起来
                    onlineData.remove(node);
                }
            }
        }

        return returnJson;
    }
    //清除Leave用户栈
    static public void ClearLeave(){
        while(!Leave.empty()){//栈非空,一个个清除数据
            onlineUser.remove(Leave.pop());
        }
    }

    //清除所有用户,数据
    static public void Clear(){
        ClearUser(onlineUser.Root);//删除用户节点
        ClearData(onlineData.Root);
    }

    private static void ClearUser(RBTNode x){
        if(x == null) return;
        ClearUser(x.left);
        RemoveOnline(((UserFile)(((OnlineData)(x.vaule)).data)).username);//这里可以判断下是不是存在再删除
        ClearUser(x.right);
    }

    private static void ClearData(RBTNode x){
        if(x == null) return;
        ClearData(x.left);
        // remove数据节点
        DataProcessor dataProcessor = ((DataProcessor)(((OnlineData)(x.vaule)).data));
        File filePath = DatabaseRBTree.searchFile(dataProcessor.name);
        SaveDataProcessor(dataProcessor,filePath);
        onlineData.remove(x);
        ClearData(x.right);
    }

    //



    //读取数据
    //获取用户信息
    static private UserFile ReadUserFile(File file){
        UserFile userFile;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(UserManager.GetUserFile(file)));
            userFile = (UserFile) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
            userFile = null;
        }
        return userFile;
    }

    //读取一个Data
    static private DataProcessor ReadDataProcessor(File file){
        DataProcessor dataProcessor = new DataProcessor();
        dataProcessor.name = file.getName();//获得name

        File tempFile = FileManager.NextFile(file,"DataItem");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
            dataProcessor.dataItem = (DataItem) ois.readObject();
            ois.close();
        } catch (Exception e) {
            dataProcessor.dataItem = new DataItem();
            System.out.println("File is empty,Creat new DataItem");
        }

        tempFile = FileManager.NextFile(file,"DataMap");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
            dataProcessor.dataMap = (DataMap) ois.readObject();
            ois.close();
        } catch (Exception e) {
            dataProcessor.dataMap = new DataMap();
            System.out.println("File is empty,Creat new DataMap");
        }
        tempFile = FileManager.NextFile(file,"DataRBTree");
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(tempFile));
            dataProcessor.dataRBTree = (DataRBTree)ois.readObject();
            ois.close();
        } catch (Exception e) {
            System.out.println("File is empty,Creat new DataRBTree");
        }
        return dataProcessor;
    }

    //保存用户数据

    static private boolean SaveUser(UserFile user){
        //存储用户信息
        try {
            FileOutputStream fileOut = new FileOutputStream(UserManager.GetUserFile(user.file));
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(user);
            out.close();
            fileOut.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    //依次存储DataProcess
    private static boolean SaveDataProcessor(DataProcessor data, File file){
        File tempFile = FileManager.NextFile(file,"DataItem");
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

    private static boolean IsDeadNode(RBTNode node){
        if(((OnlineData) (node.vaule)).data instanceof UserFile){
            if((new Date()).getTime() - ((OnlineData) (node.vaule)).cache > LeaveTime){
                return true;
            }
        }else{
            //给组织的cache指标是相关性
            if(((OnlineData) (node.vaule)).cache <= 0){
                return true;
            }
        }
        return false;
    }
}
