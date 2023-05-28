package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.domain.GetJson.GetEventData;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ServerJson.*;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/***
 * @author Administrator
 * @date 2023/3/28 9:53
 * @function 整合data辈分的东西
 */

@Component
// 三剑客数据
public class DataProcessor implements Serializable {
//    序列化和反序列化都在这里
    @Autowired
    public DataItem dataItem;
    @Autowired
    public DataMap dataMap;
    @Autowired
    public DataRBTree dataRBTree;//这个后面应该是多个DataRBTree

    public DataProcessor() {
        dataItem = new DataItem();
        dataMap = new DataMap();
        dataRBTree = new DataRBTree();
    }

    public DataProcessor(Long user,String Name,File file){
        //带着初始人的构造函数
        dataItem = new DataItem();
        dataItem.users.add(user);
        dataItem.filePath = file;
        dataItem.name = Name;
        dataMap = new DataMap();
        dataRBTree = new DataRBTree();
    }

    //  增 - 增加一个数据条/增加多个数据条/
    public boolean AddItem(GetEventData item){
        //依次更新三树

        //添加冲突树
        long id = item.begin;
        int times = 0;
        for (long end = item.begin + item.length;end  <= item.end;end += item.circle * 86400000L){
            dataRBTree.AddItem(id,end - item.length ,end);
            times++;
            if(item.circle == 0) break; //单次跳出
        }
        //添加数据原本树,更改circle的意义
        item.circle = times;
        if(!dataItem.AddItem(item)){return false;}//key值是开始时间
        //添加字符树
        dataMap.AddMap(item.title,item.begin);
        return true;
    }

    // 删,根据时间删除
    public ReturnJson DeleteItem(long begin){
        //三树中都要删除
        long id = 0;
        if((id = dataRBTree.remove(begin)) == 0){
            // 没有发现这个节点
            return new ReturnJson(false,"没有发现这个节点");
        }
        String title;
        if((title = dataItem.remove(id)) != null){
            // 该节点已经没有内容了,需要删除map中的内容
            for(Character key : title.toCharArray()){
                dataMap.RemoveMap(key,id);
            }
        }
        return new ReturnJson(true,"");
    }

    // 查找
    public List<EventItem> QueryBetween(long begin, long end){
        List<EventItem> list = new ArrayList<>();//返回的数据
        dataRBTree.Between(begin,end);
        for(int i = 0; i < dataRBTree.stack.size(); i++){
            ClashRBTNode item = dataRBTree.stack.get(i);
            if(item == null)break;
            GetEventData data = dataItem.SearchItem((Long) item.vaule);
            //这里有bug,验证dataItem非空,验证键值没错,下一步验证search逻辑,好像是叶子节点读取错误,搜索不到,是二叉树构建出现错误!!!
            if(data == null){
                System.out.println("数据错误!!!");
            }else{
                list.add(new EventItem(data.type,data.title,data.location,(Long) item.key,data.length, data.locationData,data.indexID)); //type这里不知道
            }
        }
        return list;
    }
    // 查找冲突时间之中的所有begin和end值
    // 多关键字查询
    public  List<MapSortPair> QueryMulti(String key){
        List<MapSortPair> list = new ArrayList<>();
        Map<Long, MapSortPair> map = dataMap.MultiSearch(key);
        for (Long id : map.keySet()){
            list.add(map.get(id));
        }
        //排序下返回,需要快速排序

        return list;
    }
//    打印数据结构 - debug用
    public void print(){
        System.out.println("这页数据的用户组有:");
        for(int i = 0;i < dataItem.users.size();i++){
            System.out.printf("%d ",dataItem.users.get(i));
        }
        System.out.println("----------------------------二叉树----------------------------");
        print(dataRBTree.rbtree);
        System.out.println("----------------------------数据列表----------------------------");
        print(dataItem.itemRbtree);
        System.out.println("----------------------------映射结构----------------------------");
        print(dataMap.mapRbtree);
    }
    private void print(RBTNode tree, Object begin, int direction) {

        if(tree != null) {
            if(tree.key instanceof Long){
                if(direction==0)    // tree是根节点
                    System.out.printf("%2d   (B) is root\n", tree.key);
                else                // tree是分支节点
                    System.out.printf("%2d   (%s) is %2d's %6s child\n", tree.key, tree.color==false?"R":"B", begin, direction==1?"right" : "left");
            }else{
                if(direction==0)    // tree是根节点
                    System.out.printf("%s  (B) is root\n", tree.key);
                else                // tree是分支节点
                    System.out.printf("%s  (%s) is %s's %6s child\n", tree.key, tree.color==false?"R":"B", begin, direction==1?"right" : "left");
            }
            print(tree.left, tree.key, -1);
            print(tree.right,tree.key,  1);
        }
    }
    public void print(RBTree rbTree) {
        if (rbTree.Root != null){

            print(rbTree.Root, rbTree.Root.key, 0);
        }
    }
    public void print(ClashRBTree rbTree) {
        if (rbTree.Root != null){
            print(rbTree.Root, rbTree.Root.key, 0);
        }
    }
    private void print(ClashRBTNode tree, Object begin, int direction) {

        if(tree != null) {
            if(tree.key instanceof Long){
                if(direction==0)    // tree是根节点
                    System.out.printf("%2d   (B) is root\n", tree.key);
                else                // tree是分支节点
                    System.out.printf("%2d   (%s) is %2d's %6s child\n", tree.key, tree.color==false?"R":"B", begin, direction==1?"right" : "left");
            }else{
                if(direction==0)    // tree是根节点
                    System.out.printf("%s  (B) is root\n", tree.key);
                else                // tree是分支节点
                    System.out.printf("%s  (%s) is %s's %6s child\n", tree.key, tree.color==false?"R":"B", begin, direction==1?"right" : "left");
            }
            print(tree.left, tree.key, -1);
            print(tree.right,tree.key,  1);
        }
    }

    // 查找某个组织,某个时间点的空余时间
    public ClashTime FindEasyTime (long begin,int length) {
        ClashTime time = new ClashTime();
        // 建立日历,找到当天时间
        Date date = new Date(begin);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 设定起始时间,时分秒
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        // 找到当天或者当周的时间
        long start = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, length);
        long end = calendar.getTimeInMillis();

        // 使用between找到对应的事件 - 情况一
        dataRBTree.Between(start, end);
        for(int i = 0; i < dataRBTree.stack.size(); i++){
            ClashRBTNode item = dataRBTree.stack.get(i);
            if(item == null)break;
            // 添加冲突时间段
            time.begins.add((Long) item.key);
            time.ends.add((Long) item.end);
        }
        // 判断该点前面有没有事件 - 情况二
        dataRBTree.searchNeibor(start);
        if(dataRBTree.stack != null && dataRBTree.stack.size() > 0 && dataRBTree.stack.get(0) != null){
            // 存在前边的冲突事件
            if((Long)dataRBTree.stack.get(0).end > start){
                // 存在冲突
                time.begins.set(0, (Long) dataRBTree.stack.get(0).key);
                time.ends.set(0, (Long) dataRBTree.stack.get(0).end);
            }
        }
        // 打印存在的冲突时间
        System.out.println("begins");
        System.out.println(time.begins.toString());
        System.out.println("ends");
        System.out.println(time.ends.toString());
        // 一段一段切分

        int start_index = 0;
        int end_index = 0;
        // 确定开始index
        if(time.begins.get(0) != -1L){
            start_index = 1;
            end_index = 1;
        }
        // 获得时间段
        ClashTime easyTime = new ClashTime();
        easyTime.begins.clear();
        easyTime.ends.clear();
        // 理论上 S E 互调
        if(time.begins.get(start_index) < start){
            // 这个时间段无效,需要从start开始添加一个
            start_index++;//开始序列增加
        }
        while(start_index < time.begins.size()){//start_index >= end_index
            easyTime.begins.add(time.ends.get(end_index++));
            easyTime.ends.add(time.begins.get(start_index++));
        }
        System.out.println("easyTime");
        System.out.println(easyTime.begins.toString());
        System.out.println(easyTime.ends.toString());
        return easyTime;
    }
}
