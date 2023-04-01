package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.domain.GetJson.ClassData;
import scms.domain.ServerJson.EventItem;
import scms.domain.ServerJson.RBTNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/28 9:53
 * @function 整合data辈分的东西
 */


@Component
// 三剑客数据
public class DataProcessor implements Serializable {
    File Point;//指向的指针,通过user可以直接找到
//    序列化和反序列化都在这里
    @Autowired
    DataItem dataItem;
    @Autowired
    DataMap dataMap;
    @Autowired
    DataRBTree dataRBTree;//这个后面应该是多个DataRBTree

    public DataProcessor() {
        dataItem = new DataItem();
        dataMap = new DataMap();
        dataRBTree = new DataRBTree();
    }

    //  增 - 增加一个数据条/增加多个数据条/
    public boolean AddItem(ClassData item){
    //        依次更新三树

        if(!dataItem.AddItem(item)){return false;}
    //

        long id = item.begin;
        for (long begin = item.begin;begin  <= item.end - item.length;begin += item.circle * 86400000L){

            dataRBTree.AddItem(id, begin);
            if(item.circle == 0) break; //单次跳出
        }
    //
        String key = item.title + item.location; //关键词 - 这里的location是数字,之后肯定得换成字符串,或者通过别的方式搜索
        dataMap.AddMap(key,item.begin);
        return true;
    }
//  查找
    public List<EventItem> QueryBetween(long begin, long end){
        List<EventItem> list = new ArrayList<>();//返回的数据
        dataRBTree.Between(begin,end);
        for(int i = 0; i < dataRBTree.stack.size(); i++){
            System.out.println("stack debug");
            RBTNode item = dataRBTree.stack.get(i);
            ClassData data = dataItem.SearchItem((Long)(item.vaule));//这里应该是id
            list.add(new EventItem((Long)(item.vaule),data.title,data.location,(Long) item.key,data.length));
//        装填完毕
        }
        return list;
    }


//    打印数据结构
    public void print(){
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
        if (rbTree.Root != null)
            print(rbTree.Root, rbTree.Root.key, 0);
    }
    //保存序列化数据

}
