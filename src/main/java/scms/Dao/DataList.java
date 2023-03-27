package scms.Dao;

import scms.domain.ClassData;

import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/27 19:58
 * @function 封装一个类,对数据类进行处理,增删改查
 */

//    分开维护
public class DataList extends Dao {
//    数据列表
    public ArrayList<ClassData> classData;
    public ArrayList<ClassData> personData;
//    数据对应哈希算法，需要一个唯一id对应,设置ID,获取ID
    private int SetID(int index,int type){
//     如果是class的是负数
        if(type == 0){
            return -(index+1);
        }else{
            return index+1;
        }
    }
//      根据ID返回需要的数据
    public ClassData GetData(int index){
//        需要返回class数据
        if(index < 0){
            return classData.get(-index-1);
        }else{
            return personData.get(index-1);
        }
    }

}
