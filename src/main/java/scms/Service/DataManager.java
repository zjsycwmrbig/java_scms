package scms.Service;

import scms.Dao.*;

import scms.Interceptor.BridgeData;
import scms.Interceptor.FileManager;
import scms.domain.GetJson.ClassData;

import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnEventData;
import scms.domain.ReturnJson.ReturnQueryData;
import scms.domain.ServerJson.*;

import java.io.*;

import java.util.*;


/***
 * @author Administrator
 * @date 2023/3/31 23:36
 * @function 处理多个DataProcess
 */
public class DataManager {

    public UserFile user;
//    给出该用户需要的处理器集合
    public List<DataProcessor> owner;
    public List<DataProcessor> player;

    public DataManager() {
        //只有自己才能操作自己的Data
        user = BridgeData.getRequestInfo();
        //先查在线树,后自己从内存里拿
        OnlineData onlineData = null;
        if(user!=null) onlineData = OnlineManager.GetData(user.username);
        if(onlineData != null) {
            this.owner = onlineData.data.owner;
            this.player = onlineData.data.player;
        }else{
            assert user != null;
            GetData(user);//根据user获取数据
        }
    }
    private void GetData(UserFile user){
        owner = new ArrayList<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
                owner.add(Fill(user.owner.get(i)));
            }
        }
        player = new ArrayList<>();
        if(user.player!=null){
            for(int i = 0;i < user.player.size();i++){
                player.add(Fill(user.player.get(i)));
            }
        }
    }
    public DataManager(UserFile User){ //有参构造
        user = User;
        GetData(User);
    }


    private DataProcessor Fill(File file){
//        给出的是file目录,我们要的是下面的文件
        DataProcessor dataProcessor = new DataProcessor();
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
        dataProcessor.dataRBTree = GetDatarbtree(tempFile);

        return dataProcessor;
    }

    private DataRBTree GetDatarbtree(File file){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            DataRBTree dataRBTree = (DataRBTree)ois.readObject();
            ois.close();
            return dataRBTree;
        } catch (Exception e) {
            System.out.println("File is empty,Creat new DataRBTree");
            return new DataRBTree();
        }
    }

    //  根据序号找dataProcessor的方法,后面一定废弃
    public DataProcessor GetDataProcessor(int i){//看是owner还是player
        DataProcessor data = owner.get(i);
        if(data == null) return player.get(i);
        else return data;
    }

    //  得到weekIndex的方法,是查询的辅助方法
    private int GetWeekIndex(long begin){
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(begin));
        return (now.get(Calendar.DAY_OF_WEEK)+5)%7;
    }
//    增
//    添加一个数据,校验一个
    public ReturnAddJson AddItem(ClassData item){
        int index = item.type;//得到type,代表是那一份数据
        DataProcessor data = owner.get(index);
        ReturnAddJson returnAddJson = ClashCheck(item);
        if(returnAddJson.res){
            data.AddItem(item);
        }
        return returnAddJson;
    }
//  按照owner中数据页的序号找到data的页面
    private ReturnAddJson ClashCheck(ClassData item){
        int i = item.type;
        // pre.先比较本人和这个时间的冲突
        // 1.得到所有用户数据文件
        List<Long> users = owner.get(i).dataItem.users;
        // 2.依次取出其中的dataRBtree
        List<File> usersFile = new ArrayList<>();
        for(int j = 0;j < users.size();j++){
            usersFile.add(UserRBTree.searchFile(users.get(j)));
        }
        // 3.依次比对每个用户和数据,给出数据返回,包括是否比对成功,每个用户的情况
        ReturnAddJson returnAddJson = new ReturnAddJson(true,"");
        returnAddJson.res =true;
        returnAddJson.clashList = new ArrayList<>();//新建一个
        ClashData temp = ClashCheck(usersFile.get(0),item,1);

        returnAddJson.clashList.add(temp); //先查找本人的行程

        if(temp.clashNum != 0) returnAddJson.res = false;
        for(int j = 1;j < usersFile.size();j++){
            temp = ClashCheck(usersFile.get(j),item,0);
            returnAddJson.clashList.add(temp);
            if (temp.clashNum != 0) returnAddJson.res = false;
        }
        return returnAddJson;
    }

    //    对一个用户的数据进行比对
    private ClashData ClashCheck(File user,ClassData item,int FillData){
        ClashData clashData = new ClashData();
        if(FillData == 1) clashData.list = new ArrayList<>();
        clashData.type = FillData;//给出type
        // 1.获得user文件下的所有二叉树文件
        List<DataRBTree> list = new ArrayList<>();
        List<String> name = new ArrayList<>();
        UserFile userFile;
            //读取userFile文件
        userFile = UserManager.GetUser(user);//得到userFile文件
        if(userFile == null){
            clashData.type = -1;//标记出错
            return clashData;
        }
        clashData.netName = userFile.netname;//给出名字
            //读取owner和player,给出数据数目

        for(int i = 0;i < userFile.owner.size();i++){
            list.add(GetDatarbtree(FileManager.NextFile(userFile.owner.get(i),"DataRBTree")));//拿到owner中的所有数据
            name.add(userFile.owner.get(i).getName());
        }
        if(userFile.player != null){
            for(int i = 0;i < userFile.player.size();i++){
                list.add(GetDatarbtree(FileManager.NextFile(userFile.player.get(i),"DataRBTree")));//拿到owner中的所有数据
                name.add(userFile.owner.get(i).getName());
            }
        }
        // 2.根据文件一个一个比对ClassData
        for (long end = item.begin + item.length;end  <= item.end ;end += item.circle * 86400000L){
            // 依次比对每个时间节点,把begin这个数值放到二叉树中找,看看返回的neibor
            for(int i = 0;i < list.size();i++){
                // 对于其他用户直接查看有无冲突就好,复杂度是logn
                if(FillData == 1){//主导用户
                    list.get(i).Between(end-item.length,end);//得到数据在stack里面
                    List<ClashRBTNode> stack = list.get(i).stack;
                    for(int j = 0;j < stack.size();j++){
                        clashData.list.add(new ClashItem(name.get(i),(Long)stack.get(j).key,(Long)stack.get(j).vaule,Math.max(((Long)stack.get(j).key-end),((Long)stack.get(j).key-(Long)stack.get(j).end))));
                    }
                    clashData.clashNum += stack.size();
                    // 组织名称
                }else{//不是主导用户直接记录一共有多少冲突就好
                    list.get(i).searchNeibor(end);
                    ClashRBTNode form = list.get(i).stack.get(0);
                    // 出现冲突
                    if((Long)(form.end) >= end - item.length){
                        clashData.clashNum++;
                    }
                }
            }
            if(item.circle == 0) break; //单次跳出
        }
        return clashData;
    }


//    查
    //查询当前一周的数据,并且按照规定好的数据格式返回
    public ReturnEventData QueryWeek(Date date){
        //获得user认证
        user = BridgeData.getRequestInfo();
        Calendar now = Calendar.getInstance();
        now.setTime(date);//设置时间

        // 将时分秒,毫秒域清零
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        // 计算本周的周一和周末的日期
        now.add(Calendar.DATE, -dayOfWeek + 2); // 本周的周一
        long monday = now.getTime().getTime();
        now.add(Calendar.DATE, 7); // 下周的周一
        long sunday = now.getTime().getTime();
        System.out.println(new Date(monday));
        System.out.println(new Date(sunday));
        ReturnEventData returnEventData = new ReturnEventData();
//        新建一套体系
        ArrayList<EventDataByTime> eventDataByTimes = new ArrayList<>();
        for(int i = 0;i < 7;i++){
            eventDataByTimes.add(new EventDataByTime(i));
        }

        if(owner != null){
            for(int i = 0;i < owner.size();i++){
                List<EventItem> list = owner.get(i).QueryBetween(monday,sunday);
                EventDataByType eventDataByType = new EventDataByType(user.owner.get(i).getName(),0,list);
                returnEventData.events.add(eventDataByType);
                for(int j = 0;j < list.size();j++){
                    EventItem item = list.get(j);
                    item.type = i;//给出i
                    eventDataByTimes.get(GetWeekIndex(list.get(j).begin)).list.add(item);
                }
            }
        }

        if(player != null){
            for(int i = 0;i < player.size();i++){
                List<EventItem> list = player.get(i).QueryBetween(monday,sunday);
                EventDataByType eventDataByType = new EventDataByType(user.player.get(i).getName(),1,list);
                returnEventData.events.add(eventDataByType);
                for(int j = 0;j < list.size();j++){
                    EventItem item = list.get(j);
                    item.type = -i;//给出i
                    eventDataByTimes.get(GetWeekIndex(list.get(j).begin)).list.add(item);
                }
            }
        }
//      排序对结果排序
        for(int i = 0;i < eventDataByTimes.size();i++){
            Collections.sort(eventDataByTimes.get(i).list);
        }

        returnEventData.routines = eventDataByTimes;
        return returnEventData;
    }
    //待实现
    public ReturnEventData QueryAll(){
        user = BridgeData.getRequestInfo();
        return new ReturnEventData();
    }
    //多关键字查询
    public ReturnQueryData QueryMulti(String key){
        //多文件查询
        ReturnQueryData returnQueryData = new ReturnQueryData();
        returnQueryData.list = new ArrayList<>();


        //查询owner文件
        if(owner != null){
            for(int i = 0;i < owner.size();i++){
                List<MapSortPair> list = owner.get(i).QueryMulti(key);
                for(MapSortPair mapSortPair : list){
                    ClassData data = owner.get(i).dataItem.SearchItem(mapSortPair.id);//获得数据
                    returnQueryData.list.add(new QueryEventItem(data,mapSortPair.score));
                }

            }
        }
        //查询player文件
        if(player != null){
            for(int i = 0;i < player.size();i++){
                List<MapSortPair> list = owner.get(i).QueryMulti(key);
                for(MapSortPair mapSortPair : list){
                    ClassData data = player.get(i).dataItem.SearchItem(mapSortPair.id);//获得数据
                    returnQueryData.list.add(new QueryEventItem(data,mapSortPair.score));
                }
            }
        }
        //排序
        Collections.sort(returnQueryData.list,new compareQuery());
        //填充
        return  returnQueryData;
    }

//  存储该用户文件,按照等级存储
//    public boolean Save(){
//        if(owner != null && owner.size() != 0){
//            for(int i = 0;i < owner.size();i++){
//                if(!SaveItem(owner.get(i),user.owner.get(i))) return false;//一个个保存
//            }
//        }
//
//        if(player != null && player.size() != 0){
//            for(int i = 0 ;i < player.size();i++){
//                if(!SaveItem(player.get(i),user.player.get(i)))return false;//一个个保存
//            }
//        }
//        return true;
//    }
//  分文件存储
//  --后期直接记录存储位置,按需存储,比如改动了一个文件就暂存起来
//    private  boolean SaveItem(DataProcessor data,File file){
//        File tempFile = FileManager.NextFile(file,"DataItem");
//        try {
//            FileOutputStream fileOut1 = new FileOutputStream(tempFile);
//            ObjectOutputStream out1 = new ObjectOutputStream(fileOut1);
//            out1.writeObject(data.dataItem);
//            out1.close();
//            fileOut1.close();
//        } catch (IOException i) {
//            i.printStackTrace();
//            return false;
//        }
//
//        tempFile = FileManager.NextFile(file,"DataMap");
//        try {
//            FileOutputStream fileOut2 = new FileOutputStream(tempFile);
//            ObjectOutputStream out2 = new ObjectOutputStream(fileOut2);
//            out2.writeObject(data.dataMap);
//            out2.close();
//            fileOut2.close();
//        } catch (IOException i) {
//            i.printStackTrace();
//            return false;
//        }
//
//        tempFile = FileManager.NextFile(file,"DataRBTree");
//        try {
//            FileOutputStream fileOut3 = new FileOutputStream(tempFile);
//            ObjectOutputStream out3 = new ObjectOutputStream(fileOut3);
//            out3.writeObject(data.dataRBTree);
//            out3.close();
//            fileOut3.close();
//        } catch (IOException i) {
//            i.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
}
