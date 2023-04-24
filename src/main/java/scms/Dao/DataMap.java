package scms.Dao;

import org.springframework.stereotype.Component;
import scms.domain.ServerJson.MapItem;
import scms.domain.ServerJson.MapList;
import scms.domain.ServerJson.MapSortPair;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/***
 * @author Administrator
 * @date 2023/3/27 20:11
 * @function 序列化需要的map映射类,主要用于查询以及多文件查询
 */
@Component
public class DataMap implements Serializable {
    public RBTree<MapList,Character> mapRbtree;//映射红黑树

    public DataMap() {
        mapRbtree = new RBTree<>();
    }
    //    单关键词查询,精准查询

    //    多关键词查询,字符查询

    //    map维护
    //    增
    public void AddMap(String key,Long ID){
        char[] charArray = key.toCharArray();
        for(int i = 0;i < key.length();i++){
            //查找c有没有对应的字符
            MapList list = mapRbtree.search(charArray[i]);
            char next = '\0';
            if(i!=key.length() - 1){
                next = charArray[i+1];
            }
            if(list == null){
                list = new MapList();
                list.mapList.add(new MapItem(ID,next));
                mapRbtree.insert(list,charArray[i]);
            }else{
                mapRbtree.search(charArray[i]).mapList.add(new MapItem(ID,next));
            }
        }
    }
//    删
    public  void RemoveMap(String key){
//        mapRbtree.remove(mapRbtree.searchNode(mapRbtree.Root,key));
    }

//    查
    public Map<Long, MapSortPair> MultiSearch(String key){
        Map<Long, MapSortPair> MultiRes = new HashMap<>();
        char[] charArrays = key.toCharArray();
        for(int i = 0;i < key.length();i++){
            //依次找到每个字符赋分
            MapList mapList = mapRbtree.search(charArrays[i]);
            if(mapList != null){
                //非空
                Set<MapItem> list = mapList.mapList;
                for(MapItem item : list){
                    if(!MultiRes.containsKey(item.id)) {
                        //新建映射
                        MultiRes.put(item.id,new MapSortPair(item.id, 0,1));
                    }
                    if(i != key.length()-1){
                        //如果不是最后一个考虑顺序的问题激励
                        if(charArrays[i+1] == item.next){
                            MultiRes.get(item.id).multiple = MultiRes.get(item.id).multiple<<1;//翻倍
                        }else{
                            MultiRes.get(item.id).multiple = 1;//没有激励
                        }
                    }
                    MultiRes.get(item.id).score += MultiRes.get(item.id).multiple;
                }
            }
        }
        return MultiRes;
    }
//   --模糊搜索
}
