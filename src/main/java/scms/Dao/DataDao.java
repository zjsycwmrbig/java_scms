package scms.Dao;

import scms.Interceptor.BridgeData;
import scms.domain.ClassData;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/28 9:53
 * @function
 */
// 三剑客数据
public class DataDao {
//    序列化和反序列化都在这里
    RBTree rbtree;
    DataList dataList;
    QueryMap queryMap;
//  初始化DataDao,这是最基本的一个Data类,后面初始化应该还需要多层DataDao初始化,来更新集体活动
    public boolean Init(){
//        尝试反序列化,如果没有成功则新建类为空
//        rbtree在这里序列化和反序列化
        try {
//            这里序列化和反序列化
            // 创建一个ObjectInputStream对象
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BridgeData.getRequestInfo().rbtreeData));
            // 从文件中读取Person对象
            rbtree = (RBTree) ois.readObject();
            // 打印Person对象的属性
            // 关闭流
            ois.close();
        } catch (Exception e) {
            rbtree = new RBTree();
        }
        dataList = new DataList();
        dataList.Init();
        queryMap = new QueryMap();
        queryMap.Init();

        return true;
    }

//  增 - 增加一个数据条/增加多个数据条/
    public boolean AddItem(ClassData item){
//        依次更新三剑客
        dataList.AddItem(item);

        int id = dataList.GetNewID(item.type);
        for (long i = item.begin;i  <= item.end - item.length;i += item.circle * 86400000L){
            rbtree.insert(id,i);
        }


        return true;
    }

}
