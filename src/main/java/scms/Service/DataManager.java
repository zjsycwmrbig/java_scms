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
        user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        //依次填充数据
        owner = new ArrayList<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
                owner.add(OnlineManager.GetEventData(user.owner.get(i).getName(),0L));
            }
        }
        player = new ArrayList<>();
        if(user.player!=null){
            for(int i = 0;i < user.player.size();i++){
                player.add(OnlineManager.GetEventData(user.player.get(i).getName(),0L));
            }
        }
        System.out.println("填充了" + owner.size() + "条owner数据");
        for(int i = 0;i < owner.size();i++){
            System.out.println(owner.get(i).dataItem.users.toString());
        }
        System.out.println("填充了" + player.size() + "条player数据");
        for(int i = 0;i < player.size();i++){
            System.out.println(player.get(i).dataItem.users.toString());
        }
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

//    增加一个dataProcessor
    public ReturnAddJson AddOrg(DataProcessor data){
        ReturnAddJson returnAddJson = new ReturnAddJson(true,"");
        returnAddJson.res =true;

        returnAddJson.clashList = new ArrayList<>();//新建一个
        //获得name和list
        List<DataRBTree> list = new ArrayList<>();
        List<String> name = new ArrayList<>();

        //拿到用户文件
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        //判重
        File file = DatabaseRBTree.searchFile(data.dataItem.name);
        if(userFile.owner.contains(file) || userFile.player!=null && userFile.player.contains(file)){
            returnAddJson.res = false;
            returnAddJson.state = "已加入该组织,请勿重复加入";
            return returnAddJson;
        }

        //读取owner和player,给出数据数目
        for(int i = 0;i < userFile.owner.size();i++){
            list.add(OnlineManager.GetEventData(userFile.owner.get(i).getName(),0L).dataRBTree);//拿到owner中的所有数据
            name.add(userFile.owner.get(i).getName());
        }

        if(userFile.player != null){
            for(int i = 0;i < userFile.player.size();i++){
                list.add(OnlineManager.GetEventData(userFile.player.get(i).getName(),0L).dataRBTree);//拿到owner中的所有数据 FileManager.NextFile(userFile.player.get(i),"DataRBTree")  GetDatarbtree()
                name.add(userFile.owner.get(i).getName());
            }
        }

        //依次获得每个数据的比对结果,二叉树中序遍历

        List<ClassData> itemList = GetAll(data);
        //依次比对每一条数据
        for (int i = 0;i < itemList.size();i++){
            ClashData temp = ClashCheck(itemList.get(i),name,list);
            returnAddJson.clashList.add(temp);
            if (temp.clashNum != 0) returnAddJson.res = false;
        }
        //没有冲突,添加到用户上面
        if(returnAddJson.res){
            if(userFile.player == null) userFile.player = new ArrayList<>();
            userFile.player.add(file);//添加到用户上面
            data.dataItem.users.add(userFile.username);//添加到数据上面
        }
        return returnAddJson;
    }

    //中序遍历所有数据,返回一个list
    private List<ClassData> GetAll(DataProcessor data){
        List<ClassData> list = new ArrayList<>();
        Inorder(data.dataItem.itemRbtree.Root,list);
        return list;
    }
    private void Inorder(RBTNode x,List<ClassData> list){
        if(x == null) return;
        Inorder(x.left,list);
        list.add((ClassData) (x.vaule));
        Inorder(x.right,list);
    }

//  按照owner中数据页的序号找到data的页面
//  主要是添加到本地的数据
    private ReturnAddJson ClashCheck(ClassData item){
        int i = item.type;
        // pre.先比较本人和这个时间的冲突

        // 1.得到所有用户数据文件

        List<Long> users = owner.get(i).dataItem.users;
        System.out.println(users.toString());
        // 3.依次比对每个用户和数据,给出数据返回,包括是否比对成功,每个用户的情况
        ReturnAddJson returnAddJson = new ReturnAddJson(true,"");
        returnAddJson.res =true;
        returnAddJson.clashList = new ArrayList<>();//新建一个

        ClashData temp = ClashCheck(users.get(0),item,1); //比对本人这一页

        returnAddJson.clashList.add(temp); //先查找本人的行程

        if(temp.clashNum != 0) returnAddJson.res = false;
        for(int j = 1;j < users.size();j++){
            temp = ClashCheck(users.get(j),item,0);
            returnAddJson.clashList.add(temp);
            if (temp.clashNum != 0) returnAddJson.res = false;
        }
        return returnAddJson;
    }

    // 对一个用户的数据进行比对,返回ClashData
    private ClashData ClashCheck(ClassData item,List<String> name,List<DataRBTree> list){
        ClashData clashData = new ClashData();
        for (long end = item.begin + item.length;end  <= item.end ;end += item.circle * 86400000L){
            // 依次比对每个时间节点,把begin这个数值放到二叉树中找,看看返回的neibor
            for(int i = 0;i < list.size();i++){
                // 对于其他用户直接查看有无冲突就好,复杂度是logn
                    list.get(i).Between(end-item.length,end);//得到数据在stack里面
                    List<ClashRBTNode> stack = list.get(i).stack;
                    for(int j = 0;j < stack.size();j++){
                        clashData.list.add(new ClashItem(name.get(i),(Long)stack.get(j).key,(Long)stack.get(j).vaule,Math.max(((Long)stack.get(j).key-end),((Long)stack.get(j).key-(Long)stack.get(j).end))));
                    }
                    clashData.clashNum += stack.size();
                    // 组织名称
            }

            if(item.circle == 0) break; //单次跳出
        }
        return clashData;
    }


    private ClashData ClashCheck(Long user,ClassData item,int FillData){
        ClashData clashData = new ClashData();
        if(FillData == 1) clashData.list = new ArrayList<>();
        clashData.type = FillData; //给出type
        // 1.获得user文件下的所有二叉树文件
        List<DataRBTree> list = new ArrayList<>();
        List<String> name = new ArrayList<>();
        UserFile userFile = OnlineManager.GetUserData(user,0L);

        // 读取userFile文件
        if(userFile == null){
            clashData.type = -1;//标记出错
            return clashData;
        }
        clashData.netName = userFile.netname;//给出名字

        //读取owner和player,给出数据数目
        for(int i = 0;i < userFile.owner.size();i++){
            list.add(OnlineManager.GetEventData(userFile.owner.get(i).getName(),0L).dataRBTree);//拿到owner中的所有数据
            name.add(userFile.owner.get(i).getName());
        }

        if(userFile.player != null){
            for(int i = 0;i < userFile.player.size();i++){
                list.add(OnlineManager.GetEventData(userFile.player.get(i).getName(),0L).dataRBTree);//拿到owner中的所有数据 FileManager.NextFile(userFile.player.get(i),"DataRBTree")  GetDatarbtree()
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
        user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        Calendar now = Calendar.getInstance();
        now.setTime(date);//设置时间

        // 将时分秒,毫秒域清零
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        int dayOfWeek = (now.get(Calendar.DAY_OF_WEEK) + 5)%7;

        // 计算本周的周一和周末的日期
        now.add(Calendar.DATE, -dayOfWeek); // 本周的周一
        long monday = now.getTime().getTime();

        now.add(Calendar.DATE, 7); // 下周的周一
        long sunday = now.getTime().getTime();
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
        user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
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

}
