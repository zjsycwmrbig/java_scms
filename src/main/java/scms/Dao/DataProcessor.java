package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.stereotype.Component;
import scms.domain.GetJson.ClassData;
import scms.domain.ServerJson.*;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    public boolean AddItem(ClassData item){
        //依次更新三树
        //添加数据原本树
        if(!dataItem.AddItem(item)){return false;}//key值是开始时间
        //添加冲突树
        long id = item.begin;
        for (long end = item.begin + item.length;end  <= item.end;end += item.circle * 86400000L){
            dataRBTree.AddItem(id,end - item.length ,end);
            if(item.circle == 0) break; //单次跳出
        }
        //添加字符树
        dataMap.AddMap(item.title,item.begin);
        return true;
    }
//  查找
    public List<EventItem> QueryBetween(long begin, long end){
        List<EventItem> list = new ArrayList<>();//返回的数据
        dataRBTree.Between(begin,end);
        for(int i = 0; i < dataRBTree.stack.size(); i++){
            ClashRBTNode item = dataRBTree.stack.get(i);
            if(item == null)break;
            ClassData data = dataItem.SearchItem((Long) item.vaule);
            //这里有bug,验证dataItem非空,验证键值没错,下一步验证search逻辑,好像是叶子节点读取错误,搜索不到,是二叉树构建出现错误!!!
            if(data == null){
                System.out.println("数据错误!!!");
            }else{
                list.add(new EventItem(data.type,data.title,data.location,(Long) item.key,data.length)); //type这里不知道
            }

        }
        return list;
    }

    //多关键字查询
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
    //保存序列化数据

}
