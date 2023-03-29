package scms.Dao;

import org.springframework.stereotype.Component;
import scms.Interceptor.BridgeData;
import scms.domain.ClassData;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author Administrator
 * @date 2023/3/27 20:11
 * @function 序列化需要的map映射类,主要用于查询以及多文件查询
 */
@Component
public class QueryMap implements Serializable {
//    map对应,查询key值,获得匹配值,对应多个
    public Map<String, List<Integer>> hashData;

//  理论上有两个hash,一个对应个人文件,一个对应集体文件
//  两个rbtree,



//    单关键词查询

//    多关键词查询

//    map维护
    public void Init(){
        try {
//            这里序列化和反序列化
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BridgeData.getRequestInfo().hashData));
            hashData = (Map<String, List<Integer>>) ois.readObject();
            // 关闭流
            ois.close();
        } catch (Exception e) {
            hashData = new HashMap<>(); //新建地图
        }
    }
//    map添加从关键字到序号的对应,并且这个关键字得长很多,比如 xxx-xxx-xxx-xxx,之后我们搜索的就是这里面的关键词
    public void Add(String key,int ID){
        List<Integer> list = hashData.get(key);
        if(list == null) list = new ArrayList<>();
        list.add(ID);
        hashData.put(key,list);
    }
//  保存序列化的内容
    public boolean sava(){
        try {
            FileOutputStream fileOut = new FileOutputStream(BridgeData.getRequestInfo().hashData);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(hashData);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            return false;
        }
    }
    public void print(){
        hashData.forEach((key,value)->{
            System.out.println(key+'-'+value);
        });
    }
}
