package scms.Dao;

import scms.Interceptor.BridgeData;
import scms.domain.ClassData;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/27 19:58
 * @function 封装一个类,对数据类进行处理,增删改查
 */

//    分开维护
public class DataList extends Dao {
    DataList(){
//        对这个DataList进行初始化,填充classData和personData,注意已经登录成功,所以文件必定存在

//        反序列化classData
        try {
            // 创建一个ObjectInputStream对象
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BridgeData.getRequestInfo().courseData));
            // 从文件中读取Person对象
            courseData = (ArrayList<ClassData>) ois.readObject();
            // 打印Person对象的属性
            // 关闭流
            ois.close();
        } catch (Exception e) {
            courseData = new ArrayList<>();
        }
//        反序列化personData
        try {
            // 创建一个ObjectInputStream对象
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BridgeData.getRequestInfo().activityData));
            // 从文件中读取Person对象
            activityData = (ArrayList<ClassData>) ois.readObject();
            // 打印Person对象的属性
            // 关闭流
            ois.close();
        } catch (Exception e) {
            activityData = new ArrayList<>();
        }
    }
//    数据列表
    public ArrayList<ClassData> courseData;
    public ArrayList<ClassData> activityData;
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
            return courseData.get(-index-1);
        }else{
            return activityData.get(index-1);
        }
    }

}
