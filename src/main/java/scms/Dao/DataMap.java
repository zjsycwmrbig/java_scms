package scms.Dao;

import org.springframework.stereotype.Component;
import java.io.*;


/***
 * @author Administrator
 * @date 2023/3/27 20:11
 * @function 序列化需要的map映射类,主要用于查询以及多文件查询
 */
@Component
public class DataMap implements Serializable {
    public RBTree<Long,String> mapRbtree;//映射红黑树

    public DataMap() {
        mapRbtree = new RBTree<>();
    }
    //    单关键词查询

//    多关键词查询

//    map维护
//    map添加从关键字到序号的对应,并且这个关键字得长很多,比如 xxx-xxx-xxx-xxx,之后我们搜索的就是这里面的关键词
//    增
    public void AddMap(String key,Long ID){
        mapRbtree.insert(ID,key);
    }
//    删
    public  void RemoveMap(String key){
        mapRbtree.remove(mapRbtree.searchNode(mapRbtree.Root,key));
    }

//    查
    public  void SearchMap(String key){
        mapRbtree.searchNode(mapRbtree.Root,key);
    }
//   --模糊搜索
}
