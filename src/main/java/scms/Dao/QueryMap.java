package scms.Dao;

import scms.Interceptor.BridgeData;
import scms.domain.ClassData;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @author Administrator
 * @date 2023/3/27 20:11
 * @function 序列化需要的map映射类,主要用于查询以及多文件查询
 */
public class QueryMap implements Serializable {
//    map对应,查询key值,获得匹配值,对应多个
    public Map<String, List<Integer>> hashData;
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
}
