package scms.Dao;

import scms.Interceptor.BridgeData;
import scms.domain.ClassData;

import java.io.*;
import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/27 19:58
 * @function 封装一个类,对数据类进行处理,增删改查
 */

//    分开维护
public class DataList extends Dao implements Serializable {

//    数据列表
    private ArrayList<ClassData> courseData;
    private ArrayList<ClassData> activityData;
//    数据对应哈希算法，需要一个唯一id对应,设置ID,获取ID
//    通过index得到ID
    private int GetID(int index,int type){
//     如果是class的是负数
        if(type == 0){
            return -(index);
        }else{
            return index;
        }
    }
//    通过ID得到index
    private int GetIndex(int ID){
        if(ID < 0){
            return -ID-1;
        }else{
            return ID-1;
        }
    }
//      根据ID返回需要的数据
    public ClassData GetData(int ID){
//        需要返回class数据
        if (ID < 0){
            return this.courseData.get(GetIndex(ID));
        }else{
            return this.activityData.get(GetIndex(ID));
        }
    }
//    生成增加数据时的ID，增加数据的时候需要的ID
    public int GetNewID(int type){
//        获得新的ID
        if(type == 0) return this.GetID(this.courseData.size(),type);
        else if (type < 0) return 0;
        else return this.GetID(this.activityData.size(),type);
    }
//  初始化序列
    public void Init(){
        //        对这个DataList进行初始化,填充classData和personData,注意已经登录成功,所以文件必定存在
//        反序列化classData
        try {
//            这里序列化和反序列化
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
//  增加课程数据
//    public boolean AddCourse(ClassData item){
//        return this.courseData.add(item);
//    }
//
//    public boolean AddActivity(ClassData item){
//        return this.activityData.add(item);
//    }

//    这里的type可以视作课程的优先级最高
    public boolean AddItem(ClassData item){
        if(item.type == 0){
            return this.courseData.add(item);
        }else{
            return this.activityData.add(item);
        }
    }
//  remove删除数据,直接把数据设置成无效,后期通过清理缓存实现删除 -- generate生成
    public boolean RemoveData(int ID){
        int index = GetIndex(ID);
        if (ID < 0){
            ClassData temp = this.courseData.get(index);
            temp.visible = false;// 标记一下
            this.courseData.set(index,temp);
        }else{
            ClassData temp = this.activityData.get(index);
            temp.visible = false;// 标记一下
            this.activityData.set(index,temp);
        }
        return true;
    }

    public boolean sava(){
        try {
            FileOutputStream fileOut = new FileOutputStream(BridgeData.getRequestInfo().activityData);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(activityData);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            return false;
        }
    }
//  打印数据列表
    public void print(){
        System.out.println("CourseData");
        for(int i = 0;i < courseData.size();i++){
            System.out.println(courseData.get(i).title);
        }
        System.out.println("ActivityData");
        for(int i = 0;i < activityData.size();i++){
            System.out.println(activityData.get(i).title);
        }
    }

}
