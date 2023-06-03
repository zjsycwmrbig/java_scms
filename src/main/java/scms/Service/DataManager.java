package scms.Service;

import scms.Dao.*;

import scms.Interceptor.BridgeData;
import scms.Dao.WriteLog;
import scms.domain.GetJson.GetEventData;

import scms.domain.ReturnJson.*;
import scms.domain.ServerJson.*;

import javax.naming.spi.ObjectFactory;
import java.io.*;

import java.util.*;

/***
 * @author Administrator
 * @date 2023/3/31 23:36
 * @function 处理多个DataProcess
 */
public class DataManager {
    //  数据
    public UserFile user;
    public List<DataProcessor> owner;
    public List<DataProcessor> player;
    //    初始化
    public DataManager() {
        //依次填充数据
        user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        if (user == null) {
            System.out.println("出错!");
            return;
        }
        owner = new ArrayList<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
                owner.add(OnlineManager.GetEventData(user.owner.get(i),0L));
            }
        }
        player = new ArrayList<>();
        if(user.player!=null){
            for(int i = 0;i < user.player.size();i++){
                player.add(OnlineManager.GetEventData(user.player.get(i),0L));
            }
        }
        System.out.println("填充了" + owner.size() + "条owner数据");

        for(int i = 0;i < owner.size();i++){
            System.out.println(owner.get(i).dataItem.name);
            System.out.println(owner.get(i).dataItem.users.toString());
        }
        System.out.println("填充了" + player.size() + "条player数据");
        for(int i = 0;i < player.size();i++){
            System.out.println(player.get(i).dataItem.name);
            System.out.println(player.get(i).dataItem.users.toString());
        }
    }
    //    增
//    添加一个数据,校验一个
    public ReturnAddJson AddItem(GetEventData item){
        // 得到indexID,代表是那一份数据
        int index = item.indexID;
        DataProcessor data = owner.get(index);
        // 给出组名，不在前端给
        item.group = data.dataItem.name;
        // 冲突返回结果
        ReturnAddJson returnAddJson = null;
        // 判断是否是临时事件
        if(item.type == 2){
            returnAddJson = InsertTempEvent(item);
            System.out.println("采用临时事务插入方法");
        }else{
            returnAddJson = AddClashCheck(item,item.indexID);
            // 添加数据
            if(returnAddJson.res){
                data.AddItem(item);
            }
        }
        // 得到组织信息,向所有人的日志中写入信息
        DataProcessor group = OnlineManager.GetEventData(item.group, 0L);

        if(group != null){
            List<Long> users = group.dataItem.users;
            UserFile userFile;
            for (Long aLong : users) {
                userFile = OnlineManager.GetUserData(aLong, 0L);
                WriteLog.writeLog(userFile,returnAddJson.res,"AddItem",item.title);
            }
        }

        return returnAddJson;
    }

    //添加临时事件
    private ReturnAddJson InsertTempEvent(GetEventData tempEvent){
        // 这个事件左右最多存在的事件有下面几个来源
        // 1. begin和end在这个事件之间的事件
        // 2. begin左边存在end值大于begin的事件

        // 获得冲突数据
        ReturnAddJson tempClash = AddClashCheck(tempEvent, tempEvent.indexID);
        DataProcessor data = owner.get(tempEvent.indexID);
        if(tempClash.res){
            // 不存在冲突
            System.out.println("不存在冲突=======");
            data.AddItem(tempEvent);
        }else{
            //存在一定的冲突
            if(tempClash.clashList != null){
                if(tempClash.clashList.size() == 1){//多页不可合并
                    //冲突的是owner的数据,那么就是owner的数据有问题,有机会合并
                    List<ClashItem> clashList = tempClash.clashList.get(0).list;
                    //如果这些事件的类型都是临时事件,那么就可以合并,那么这里的
                    // 找到事件的开始结束
                    long startBegin = tempEvent.begin;
                    long startEnd = tempEvent.end;
                    for(ClashItem clashItem : clashList){
                        if(clashItem.type != 2){
                            //不是临时事件,那么就没法合并
                            tempClash.state = "含有非临时事件,无法合并";
                            return tempClash;
                        }else{
                            startBegin = Math.min(startBegin,clashItem.time);
                            startEnd = Math.max(startEnd,clashItem.time);
                        }
                    }
                    // 满足合并条件,进行合并准备,获取临时事件集合列表
                    List<EventItem> temps = owner.get(0).QueryBetween(startBegin,startEnd);
                    // 得到真正的临时事件
                    GetEventData realTemp = tempEvent;
                    // 添加信息
                    for (EventItem temp : temps) {
                        // 划分开
                        realTemp.title = realTemp.title + "|" + temp.title;//回车分割
                        realTemp.location = realTemp.location + "|" + temp.location;//分割
                        realTemp.locationData = realTemp.locationData + "|" + temp.locationData;
                        // 获得开始结束
                        realTemp.begin = Math.min(realTemp.begin,temp.begin);
                        realTemp.end = Math.max(realTemp.end,temp.begin + temp.length);
                        realTemp.length = realTemp.end - realTemp.begin;
                    }
                    // 删除原有的临时事件
                    for (EventItem temp : temps) {
                        data.DeleteItem(temp.begin);//理论上删除所有的临时事件了
                    }
                    // 添加新的临时事件
                    data.AddItem(realTemp);
                    tempClash.res = true;
                    tempClash.state = "临时事件合并成功";
                }else{
                    //没法合并,自认倒霉
                    tempClash.state = "临时事件无法合并";
                    return tempClash;
                }
            }
        }
        return tempClash;
    }
    //    增加一个dataProcessor,也就是添加组织
    public ReturnAddJson AddOrg(DataProcessor data){
        ReturnAddJson returnAddJson = new ReturnAddJson(true,"");
        returnAddJson.clashList = new ArrayList<>();//新建一个
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
//        //判重
        File file = DatabaseManager.searchFile(data.dataItem.name);
        if(userFile.owner.contains(file) || userFile.player != null && userFile.player.contains(file)) {
            returnAddJson.res = false;
            returnAddJson.state = "已加入该组织,请勿重复加入";
            return returnAddJson;
        }
        List<GetEventData> itemList = GetAllDataItem(data);//把数据依次添加到list中
//        //依次比对每一条数据
        for (int i = 0;i < itemList.size();i++){
            returnAddJson = AddClashCheck(itemList.get(i),0);//作为0页比较
            if(!returnAddJson.res) break;
        }
//        //没有冲突,添加到用户上面
        if(returnAddJson.res){
            if(userFile.player == null) userFile.player = new ArrayList<>();
            userFile.player.add(file);//添加到用户上面
            data.dataItem.users.add(userFile.username);//添加到数据上面
        }

        return returnAddJson;
    }


    //  添加时冲突检测,indexID是想要添加到哪一个数据页面
    private ReturnAddJson AddClashCheck(GetEventData item,int indexID){
        ReturnAddJson returnAddJson = new ReturnAddJson(true,"");
        returnAddJson.clashList = new ArrayList<>();//新建一个冲突列表
        // 添加到哪一个数据页
        int i = indexID;
        // 1.得到所有用户数据文件,包括自身和其他使用成员

        List<Long> users = owner.get(i).dataItem.users;

        // 2.依次比对每个用户和数据,给出数据返回,包括是否比对成功,每个用户的情况

        ClashData ownerClashData = ClashCheckPerson(users.get(0),item,1); //比对本人这一页

        returnAddJson.clashList.add(ownerClashData); //先查找本人的行程

        if(ownerClashData.clashNum != 0) returnAddJson.res = false;

        // 其他用户对比
        for(int j = 1;j < users.size();j++){
            ownerClashData = ClashCheckPerson(users.get(j),item,0);
            returnAddJson.clashList.add(ownerClashData);
            if (ownerClashData.clashNum != 0) returnAddJson.res = false;
        }
        return returnAddJson;
    }

    // 根据一个账号,得到这个账号的所有数据,比对所有的数据
    private ClashData ClashCheckPerson(Long user, GetEventData item, int FillData){//用户名,待比对数据,是否补充详细信息
        ClashData clashData = new ClashData();
        if(FillData == 1) clashData.list = new ArrayList<>();//补充详细信息

        clashData.isOwner = FillData; //给出type
        // 1.获得user文件下的所有二叉树文件
        List<DataRBTree> list = new ArrayList<>();
        List<String> name = new ArrayList<>();

        UserFile userFile = OnlineManager.GetUserData(user,0L);
        if(userFile == null){
            clashData.isOwner = -114514;//标记出错
            System.out.println("该用户不存在,比对时错误");
            return clashData;
        }
        // 标记名称

        clashData.netName = userFile.netname;//给出名字
        clashData.username = userFile.username;//给出用户名
        //读取owner和player,给出数据数目,填充名字
        for(int i = 0;i < userFile.owner.size();i++){
            DataProcessor data = OnlineManager.GetEventData(userFile.owner.get(i).getName(),0L);
            list.add(data.dataRBTree);//拿到owner中的所有数据
            name.add(data.dataItem.name);
        }

        if(userFile.player != null){
            for(int i = 0;i < userFile.player.size();i++){
                DataProcessor data = OnlineManager.GetEventData(userFile.player.get(i).getName(),0L);
                list.add(data.dataRBTree);
                name.add(data.dataItem.name);
            }
        }

        // 2.根据文件一个一个比对ClassData
        for (long end = item.begin + item.length;end  <= item.end ;end += item.circle * 86400000L){
            // 依次比对每个数据页,把begin这个数值放到二叉树中找,看邻居和范围
            for(int i = 0;i < list.size();i++){
                if(FillData == 1){//填充详细信息
                    List<ClashRBTNode> stack;
                    // 特判前面节点,指的是开始节点的前面
                    list.get(i).searchNeibor(end - item.length);
                    stack = list.get(i).stack;
                    if(stack.get(0) != null){
                        // 存在冲突数据
                        if((Long)(stack.get(0).end) > end - item.length){
                            clashData.clashNum++;
                            // 根据vaule值找到数据信息
                            GetEventData data = GetDataProcessor(i).dataItem.SearchItem((Long)stack.get(0).vaule);
                            if(data == null) System.out.println("data is null");
                            else{
                                // 填充信息
                                clashData.list.add(new ClashItem(name.get(i),(Long)stack.get(0).key,data.type,Math.max(((Long)stack.get(0).key-end),((Long)stack.get(0).key-(Long)stack.get(0).end)),data.title));
                            }
                        }
                    }
                    // 排除掉冲突在内部的以及后面的可能
                    list.get(i).Between(end-item.length,end);
                    // 得到数据
                    stack = list.get(i).stack;

                    for(int j = 0;j < stack.size();j++){
                        clashData.clashNum++;
                        GetEventData data = GetDataProcessor(i).dataItem.SearchItem((Long)stack.get(j).vaule);
                        if(data == null) System.out.println("data is null");
                        else{
                            clashData.list.add(new ClashItem(name.get(i),(Long)stack.get(j).key,data.type,Math.max(((Long)stack.get(j).key-end),((Long)stack.get(j).key-(Long)stack.get(j).end)),data.title));
                        }
                    }
                }else{
                    //不是增加数据的用户直接记录有冲突就好

                    // 只在乎前面的节点会不会有冲突
                    list.get(i).searchNeibor(end);
                    ClashRBTNode form = list.get(i).stack.get(0);
                    if(form == null){
                        continue;//说明没有冲突
                    }else{
                        // 出现冲突,直接继续就好
                        if((Long)(form.end) >= end - item.length){
                            clashData.clashNum++;
                            return clashData;
                        }
                    }
                }
            }
            //单次跳出
            if(item.circle == 0) break;
        }
        return clashData;
    }

    //    查
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
        long sunday = now.getTime().getTime() -1 ;

        ReturnEventData returnEventData = new ReturnEventData();

        int total = 0;

        //新建一套星期体系
        ArrayList<EventDataByTime> eventDataByTimes = new ArrayList<>();
        for(int i = 0;i < 7;i++){
            eventDataByTimes.add(new EventDataByTime(i));
        }

        if(owner != null){
            for(int i = 0;i < owner.size();i++){
                System.out.println("查询" + owner.get(i).dataItem.name);
                List<EventItem> list = owner.get(i).QueryBetween(monday,sunday);
                for(int j = 0;j < list.size();j++){
                    EventItem item = list.get(j);
                    item.group = owner.get(i).dataItem.name;
                    eventDataByTimes.get(GetWeekIndex(list.get(j).begin)).list.add(item);
                    total++;
                }
            }
        }

        if(player != null){
            for(int i = 0;i < player.size();i++){
                List<EventItem> list = player.get(i).QueryBetween(monday,sunday);
                for(int j = 0;j < list.size();j++){
                    EventItem item = list.get(j);
                    item.indexID = -i - 1;//标记为player
                    item.group = player.get(i).dataItem.name;
                    eventDataByTimes.get(GetWeekIndex(list.get(j).begin)).list.add(item);
                    total++;
                }
            }
        }
        //排序对结果排序
        for(int i = 0;i < eventDataByTimes.size();i++){
            SortFast.fun(eventDataByTimes.get(i).list,null); //这个比较器传null不知道行不行

        }

        // 装填是否含有闹钟
        for(int i = 0;i < eventDataByTimes.size();i++){
            for(int j = 0;j < eventDataByTimes.get(i).list.size();j++){
                assert user != null;
                if (user.Exist(eventDataByTimes.get(i).list.get(j).begin)){
                    eventDataByTimes.get(i).list.get(j).alarmFlag = true;
                }else{
                    eventDataByTimes.get(i).list.get(j).alarmFlag = false;
                }
            }
        }

        returnEventData.routines = eventDataByTimes;
        returnEventData.total = total;
        WriteLog.writeLog(user,returnEventData.res,"QueryNow","");
        return returnEventData;
    }
    // 查询从事件到到哪
    public Return<List<EventItem>> QueryBetween(long begin,long end){
        List<EventItem> res = new ArrayList<>();
        // 一个一个的找到
        t(begin, end, res, owner,0);
        t(begin, end, res, player,1);
        // 排序 对结果排序
        SortFast.fun(res,null);

        // 装填是否含有闹钟
        for(int i = 0;i < res.size();i++){
            assert user != null;
            if (user.Exist(res.get(i).begin)){
                res.get(i).alarmFlag = true;
            }else{
                res.get(i).alarmFlag = false;
            }
        }
        return new Return(true,"查询完毕",res);
    }
    // 提取的函数
    private void t(long begin, long end, List<EventItem> res, List<DataProcessor> player,int type) {
        for(int i = 0; i < player.size(); i++){
            List<EventItem> list = player.get(i).QueryBetween(begin,end);
            for(int j = 0;j < list.size();j++){
                // 区分是player还是owner
                if(type == 0){
                    list.get(j).indexID = i;
                }else{
                    list.get(j).indexID = -i - 1;
                }
                res.add(list.get(j));
            }
        }
    }

    //待实现
    public ReturnEventData QueryAll(){
        user = OnlineManager.GetUserData(BridgeData.getRequestInfo(),1L);
        ReturnEventData returnEventData = new ReturnEventData();
        ArrayList<EventDataByType> arrayList = new ArrayList<>();
        if(owner != null){
            for(int i = 0;i < owner.size();i++){
                //todo：写一个中序遍历冲突树，获取所有课程EventItem

            }
        }

        if(player != null){
            for(int i = 0;i < player.size();i++){
                List<GetEventData> list = GetAllDataItem(owner.get(i)); //中序遍历

            }
        }
        //WriteLog.writeQueryLog(user, returnEventData.res,"QueryAll",null);
        WriteLog.writeLog(user, returnEventData.res,"QueryAll","");
        return returnEventData;
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
                    GetEventData data = owner.get(i).dataItem.SearchItem(mapSortPair.id);//获得数据
                    returnQueryData.list.add(new QueryEventItem(data,mapSortPair.score));
                }
            }
        }
        //查询player文件
        if(player != null){
            for(int i = 0;i < player.size();i++){
                List<MapSortPair> list = owner.get(i).QueryMulti(key);
                for(MapSortPair mapSortPair : list){
                    GetEventData data = player.get(i).dataItem.SearchItem(mapSortPair.id);//获得数据
                    // 标记为player
                    data.indexID = -i - 1;
                    returnQueryData.list.add(new QueryEventItem(data,mapSortPair.score));
                }
            }
        }
        //排序
        SortFast.fun(returnQueryData.list,new compareQuery());
        //Collections.sort(returnQueryData.list,new compareQuery());
        //填充
        // 装填是否含有闹钟
        for(int i = 0;i < returnQueryData.list.size();i++){
            assert user != null;
            if (user.Exist(returnQueryData.list.get(i).item.begin)){
                returnQueryData.list.get(i).item.alarmFlag = true;
            }else{
                returnQueryData.list.get(i).item.alarmFlag = false;
            }
        }

        boolean res = true;
        if(returnQueryData.list.isEmpty())
            res = false;
        //WriteLog.writeQueryLog(userFile,res,"QueryKey",key);
        WriteLog.writeLog(user,res,"QueryKey",key);
        return  returnQueryData;
    }

    public ReturnQueryData QueryExact(String key){
        ReturnQueryData returnQueryData;
        returnQueryData = QueryMulti(key); //先采用普通搜索得到大致匹配的list
        QueryEventItem temp;
        for (int i = 0; i < returnQueryData.list.size();) {
            temp = returnQueryData.list.get(i);
            int index = KMP.getKMPIndex(temp.item.title,key);
            if(index < 0) {
                returnQueryData.list.remove(i);
                continue;//i不能变，否则会漏掉一些对象
            }
            i++;
        }
        return returnQueryData;
    }

    // 查询某一个组织在date的时候的空闲时间,也就是查找一个组织的所有人的空闲时间的交集
    public Return<ClashTime> QueryFreeTime(String org,long date,int length){
        // 比对这个数据页
        Return<ClashTime> clashTimeReturn;
        DataProcessor dataProcessor = OnlineManager.GetEventData(org,0L);
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        if (dataProcessor == null) {
            clashTimeReturn = new Return<>(false,"没有这个组织",null);
            WriteLog.writeLog(userFile,clashTimeReturn.res,"QueryFreeTime",org+"("+ clashTimeReturn.state +")");
            return clashTimeReturn;
        }
        List<Long> users = dataProcessor.dataItem.users; // 用户信息,第一个必定是本人
        // 哈希表,标记某组织是否已经查询过了
        HashMap<String, Boolean> vis = new HashMap<>();
        // 先获得组织拥有者的空闲时间
        Return<ClashTime> res = QueryFreeTime(users.get(0),date,length,vis);
        if (!res.res){
            return new Return<>(false,"用户不存在",null);
        }
        ClashTime freeTime = res.data;
        if(freeTime == null) return new Return<>(true,"无空闲时间",null);

        // 依次获得其他用户的空闲时间,实现交集处理
        for(int i = 1;i < users.size();i++){
            res =  QueryFreeTime(users.get(i),date,length,vis);
            if (!res.res){
                return new Return<>(false,"用户不存在",null);
            }
            if (res.data == null) return new Return<>(true,"无空闲时间",null);
//            if(res.data.begins.get(0) == date && res.data.ends.get(0) == date + length * 24 * 60 * 60 *1000L){
//                // 完全时间
//                continue;
//            }
            freeTime.interSet(res.data);
        }
        if (freeTime != null && freeTime.begins != null && freeTime.begins.size() != 0){
            clashTimeReturn = new Return<>(true,"",freeTime);
            WriteLog.writeLog(userFile, clashTimeReturn.res, "QueryFreeTime",org);
            return clashTimeReturn;
        }
        else{
            clashTimeReturn = new Return<>(false,"没有空闲时间",null);
            WriteLog.writeLog(userFile,clashTimeReturn.res,"QueryFreeTime",org+"("+ clashTimeReturn.state +")");
            return clashTimeReturn;
        }
    }

    // 根据用户名称查找某用户在一定时间内的空闲时间,返回的是一个时间段的列表
    public Return<ClashTime> QueryFreeTime(long user,long date,int length) {
        return QueryFreeTime(user,date,length,new HashMap<>());
    }

    private static Return<ClashTime> QueryFreeTime(long user, long date, int length, HashMap<String, Boolean> vis) {
        // 初始化 区间是 date - date + length

        // 仅仅适用作为时间查询
        ClashTime freeTime = new ClashTime();
        freeTime.normal();// 仅仅作为时间组织
        System.out.println("date = " + new Date(date) + " length = " + length);
        freeTime.begins.add(date);
        freeTime.ends.add(date + length*24*60*60*1000L);
        System.out.println("freeTime = " + freeTime.begins.toString());

        List<DataProcessor> dataProcessors = null;
        // 拿到用户数据
        UserFile userFile = OnlineManager.GetUserData(user, 0L);
        if(userFile == null) return new Return<>(false,"没有这个用户",null);

        // 整合两者之间的数据
        for (int i = 0;i < userFile.owner.size();i++) {
            if(vis.containsKey(userFile.owner.get(i).getName())) continue; // 已经存在,处理过了
            DataProcessor data = OnlineManager.GetEventData(userFile.owner.get(i).getName(), 0L);
            if(data == null) continue; // 组织不存在
            ClashTime res = data.FindEasyTime(date, length);

            if(res.begins.get(0) == date && res.ends.get(0)==date + length*24*60*60*1000L)continue;
            freeTime.interSet(res);//使用交集操作,处理时间

            vis.put(userFile.owner.get(i).getName(), true);// 标记
        }
        if(userFile.player == null) return new Return<>(true,"",freeTime);
        for (int i = 0;i < userFile.player.size();i++) {
            if(vis.containsKey(userFile.player.get(i).getName())) continue; // 已经存在,处理过了
            DataProcessor data = OnlineManager.GetEventData(userFile.player.get(i).getName(), 0L);
            if(data == null) continue; // 组织不存在
            ClashTime res = data.FindEasyTime(date, length);
            if(res.begins.get(0) == date && res.ends.get(0)==date + length*24*60*60*1000L)continue;
            freeTime.interSet(res);
            vis.put(userFile.player.get(i).getName(), true);// 标记
        }
        return  new Return<>(true,"",freeTime);
    }

    // 删
    public ReturnJson deleteItem(GetEventData item){
        ReturnJson returnJson = new ReturnJson(true,"");
        int index = item.indexID;
        DataProcessor data = owner.get(index);
        ReturnJson deleteRes = data.DeleteItem(item.begin);
        if(deleteRes.res){
            returnJson.state = "删除成功";
        }
        else {
            returnJson.res = false;
            returnJson.state ="删除失败";
        }
        DataProcessor group = OnlineManager.GetEventData(data.dataItem.name, 0L);
        if(group != null){
            List<Long> users = group.dataItem.users;
            UserFile userFile;
            for (Long aLong : users) {
                userFile = OnlineManager.GetUserData(aLong, 0L);
                //WriteLog.writeDeleteLog(userFile,returnJson.res,"DeleteItem");
                WriteLog.writeLog(userFile,returnJson.res,"DeleteItem",item.title);
            }
        }
        return returnJson;
    }
    // 删除某个组织的某个事件,必须有权限
    public ReturnJson deleteItem(long begin,int indexID){
        // 删除indexID对应的的节点数据
        DataProcessor data =  owner.get(indexID);
        ReturnJson returnJson = data.DeleteItem(begin);
        DataProcessor group = OnlineManager.GetEventData(data.dataItem.name,0L);
        // 检查该节点是不是重要时刻,如果是,则删除
        if(user.Exist(begin)){
            user.DeleteAlarm(begin);
        }
        //该组织中每个人都要记录日志
        if(group!=null){
            List<Long> users = group.dataItem.users;
            UserFile userFile;
            for (Long aLong : users) {
                userFile = OnlineManager.GetUserData(aLong, 0L);
                if(userFile != null)
                    WriteLog.writeLog(userFile, returnJson.res,"DeleteItem","");
            }
        }
        return returnJson;
    }
    // 删除某个组织
    public Return<Object> deleteOrg(String org){
        // 删除某个组织
        // 1. 权限验证
        DataProcessor dataProcessor = OnlineManager.GetEventData(org,0L);
        if(dataProcessor == null) return new Return<>(false,"没有这个组织",null);

        if(!user.owner.remove(dataProcessor.dataItem.filePath)){
            return new Return<>(false,"没有权限",null);
        };
        // 2. 删除组织,用户删除组织,依次把组织中的所有用户删除
        for(int i = 1;i < dataProcessor.dataItem.users.size();i++){
            removeOrg(dataProcessor.dataItem.users.get(i),org);
        }
        // 3. 删除组织
        DeleteDir(dataProcessor.dataItem.filePath);
        // 4. 删除组织树中的数据
        DatabaseManager.Remove(org);
        // 5. 删除缓存中的数据
        OnlineManager.RemoveOrg(org);
        WriteLog.writeLog(user,true,"deleteOrg",org);
        return new Return<>(true,"",null);
    }
    // 移除组织成员
    public Return<Object> removeOrgMember(String org,long member){
        // 1. 权限验证
        DataProcessor dataProcessor = OnlineManager.GetEventData(org,0L);
        if (dataProcessor == null) return new Return<>(false,"没有这个组织",null);
        if(user.owner.indexOf(dataProcessor.dataItem.filePath) == -1){
            return new Return<>(false,"没有权限",null);
        }
        if(dataProcessor.dataItem.users.get(0) == member){
            return new Return<>(false,"不能移除组织创建者",null);
        }
        // 2. 删除组织中的成员
        removeOrg(member,org);
        // 3. 添加日志
        WriteLog.writeLog(user,true,"removeOrgMember",org);

        return new Return<>(true,"",null);
    }
    // 更改组织口令
    public Return<Object> changeOrgPassword(String org,String password){
        // 1. 权限验证
        DataProcessor dataProcessor = OnlineManager.GetEventData(org,0L);
        if (dataProcessor == null) return new Return<>(false,"没有这个组织",null);
        if(user.owner.indexOf(dataProcessor.dataItem.filePath) == -1){
            return new Return<>(false,"没有权限",null);
        }
        // 2. 修改口令
        dataProcessor.dataItem.password = password;
        // 3. 添加日志
        WriteLog.writeLog(user,true,"changeOrgPassword",org);
        return new Return<>(true,"",null);
    }


    // 移除某个组织,不删除源文件
    public Return<Object> removeOrg(long user,String org){
        // 删除indexID对应的数据
        DataProcessor data = OnlineManager.GetEventData(org,0L);
        // 组织删除个人
        data.dataItem.users.remove(user);
        // 个人删除组织
        UserFile userFile = OnlineManager.GetUserData(user,0L);
        if(userFile == null) return new Return<>(false,"没有这个用户",null);
        // 删除组织
        if(!userFile.owner.remove(data.dataItem.filePath)){
            userFile.player.remove(data.dataItem.filePath);
        };
        // 帮助这个用户删除闹钟? 没必要,因为闹钟是从事件找闹钟
        WriteLog.writeLog(userFile,true,"removeOrg",org);
        return new Return<>(true,"",null);
    }
    //工具类

    //星期序号获得
    private int GetWeekIndex(long begin){
        Calendar now = Calendar.getInstance();
        now.setTime(new Date(begin));
        return (now.get(Calendar.DAY_OF_WEEK)+5)%7;
    }
    // 插入临时事件需要特殊处理,因为临时事件是和其他的类型有所区别,可以存在兼并属性

    //中序遍历所有数据,返回一个list,查询全部,方便看一个组织可不可以加入
    private List<GetEventData> GetAllDataItem(DataProcessor data){
        List<GetEventData> list = new ArrayList<>();
        Inorder(data.dataItem.itemRbtree.Root,list);
        return list;
    }

    private void Inorder(RBTNode x,List<GetEventData> list){
        if(x == null) return;
        Inorder(x.left,list);
        list.add((GetEventData) (x.vaule));
        Inorder(x.right,list);
    }

    // 工具类,根据i返回owner或者player
    private DataProcessor GetDataProcessor(int i){
        if(i < owner.size()) return owner.get(i);
        else return player.get(i-owner.size());
    }
    // 删除目录
    private void DeleteDir(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File file1 : files) {
                DeleteDir(file1);
            }
        }
        file.delete();
    }
}

