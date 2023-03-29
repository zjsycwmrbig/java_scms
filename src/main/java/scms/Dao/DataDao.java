package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.Service.DataRBTree;
import scms.domain.ClassData;
import scms.domain.ItemData;
import scms.domain.RBTNode;

import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/28 9:53
 * @function
 */
@Component
// 三剑客数据
public class DataDao {
//    序列化和反序列化都在这里
    @Autowired
    DataList dataList;
    @Autowired
    QueryMap queryMap;
    @Autowired
    DataRBTree dataRBTree;
//  初始化DataDao,这是最基本的一个Data类,后面初始化应该还需要多层DataDao初始化,来更新集体活动
    public boolean Init(){
        dataList = new DataList();
        dataRBTree = new DataRBTree();
        queryMap = new QueryMap();
        dataList.Init();
        dataRBTree.Init();
        queryMap.Init();
        return true;
    }

//  增 - 增加一个数据条/增加多个数据条/
    public boolean AddItem(ClassData item){
//        依次更新三剑客
        dataList.AddItem(item);

        int id = dataList.GetNewID(item.type);
        for (long i = item.begin;i  <= item.end - item.length;i += item.circle * 86400000L){
            dataRBTree.AddItem(id,i);
            if(item.circle == 0) break; //单次跳出
        }

        String key = item.title + "-" + item.location; //关键词 - 这里的location是数字,之后肯定得换成字符串,或者通过别的方式搜索
        queryMap.Add(key,id);
        return true;
    }

    public List<ItemData> QueryBetween(long begin, long end){
        List<ItemData> list = new ArrayList<>();
        dataRBTree.Between(begin,end);
        for(int i = 0; i < dataRBTree.stack.size(); i++){
            RBTNode item = dataRBTree.stack.get(i);
            ClassData data = dataList.GetData(item.id);
            list.add(new ItemData(item.id,data.title,data.type,data.location,item.key,data.length));
//        装填完毕
        }
        return list;
    }


//    打印数据结构
    public void print(){
        System.out.println("----------------------------二叉树----------------------------");
        dataRBTree.print();
        System.out.println("----------------------------数据列表----------------------------");
        dataList.print();
        System.out.println("----------------------------映射结构----------------------------");
        queryMap.print();
    }
    //保存序列化数据
    public boolean Save(){
        dataRBTree.sava();
        queryMap.sava();
        dataList.sava();
        return true;
    }
}
