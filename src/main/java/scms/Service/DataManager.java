package scms.Service;

import scms.Dao.DataProcessor;

import scms.Interceptor.BridgeData;
import scms.domain.GetJson.ClassData;

import scms.domain.ReturnJson.ReturnEventData;
import scms.domain.ServerJson.EventData;
import scms.domain.ServerJson.UserFile;

import java.io.*;

import java.util.*;


/***
 * @author Administrator
 * @date 2023/3/31 23:36
 * @function 处理多个DataProcess
 */
public class DataManager {
    public UserFile user;
//    给出该用户需要的处理器集合
    public List<DataProcessor> owner;
    public List<DataProcessor> player;

    public List<File> changeStack;//改动栈,改动的才保存

    public DataManager() {
        user = BridgeData.getRequestInfo();
        owner = new ArrayList<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
//                owner.put(user.owner.get(i),Fill(user.owner.get(i)));
                owner.add(Fill(user.owner.get(i)));

            }
        }
        player = new ArrayList<>();
        if(user.player!=null){
            for(int i = 0;i < user.player.size();i++){
                player.add(Fill(user.player.get(i)));
            }
        }
    }

//    public boolean Init(){//给出data的文件指针，找到位置
//        user = BridgeData.getRequestInfo();
//        owner = new HashMap<>();
//        if(user.owner!=null){
//            for(int i = 0;i < user.owner.size();i++){
//                owner.put(user.owner.get(i),Fill(user.owner.get(i)));
//            }
//        }
//        player = new HashMap<>();
//        if(user.owner!=null){
//            for(int i = 0;i < user.player.size();i++){
//                owner.put(user.player.get(i),Fill(user.player.get(i)));
//            }
//        }
//        if(owner == null) return false;
//
//        return true;
//    }
//  反序列化DataProcessor
    private DataProcessor Fill(File file){
//        给出的是file目录,我们要的是下面的文件
        DataProcessor dataProcessor = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
             dataProcessor = (DataProcessor) ois.readObject();
            ois.close();
        } catch (Exception e) {
            dataProcessor = new DataProcessor();
        }
        return dataProcessor;
    }
//
    public DataProcessor GetDataProcessor(int i){//看是owner还是player
        //给出序号
        DataProcessor data = owner.get(i);
        if(data == null) return player.get(i);
        else return data;
    }


//    增
//    添加一个数据,校验一个
    public boolean AddItem(int i, ClassData item){
        DataProcessor data = owner.get(i);
        return data.AddItem(item);
    }

//    查
    public ReturnEventData QueryWeek(Date date){
        user = BridgeData.getRequestInfo();
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        // 获得当前日期是本周的第几天（1代表周日，2代表周一，以此类推）
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        // 计算本周的周一和周末的日期
        now.add(Calendar.DATE, -dayOfWeek + 2); // 本周的周一
        long monday = now.getTime().getTime();
        now.add(Calendar.DATE, 7); // 下周的周一
        long sunday = now.getTime().getTime();
        ReturnEventData returnEventData = new ReturnEventData();
        System.out.printf("%d %d",monday,sunday);
        if(owner != null){
            for(int i = 0;i < owner.size();i++){
                EventData eventData = new EventData(user.owner.get(i).getName(),0,owner.get(i).QueryBetween(monday,sunday));
                returnEventData.events.add(eventData);
            }
        }

        if(player != null){
            for(int i = 0;i < player.size();i++){
                EventData eventData = new EventData(user.player.get(i).getName(),1,player.get(i).QueryBetween(monday,sunday));
                returnEventData.events.add(eventData);
            }
        }
        return returnEventData;
    }


//  存储该用户的所有文件
    public boolean Save(){
        if(owner != null && owner.size() != 0){
            for(int i = 0;i < owner.size();i++){
                if(!SaveItem(owner.get(i),user.owner.get(i))) return false;//一个个保存
            }
        }

        if(player != null && player.size() != 0){
            for(int i = 0 ;i < player.size();i++){
                if(!SaveItem(player.get(i),user.player.get(i)))return false;//一个个保存
            }
        }

        return true;
    }
//  分文件存储
//  --后期直接记录存储位置,按需存储,比如改动了一个文件就暂存起来
    private  boolean SaveItem(DataProcessor data,File file){
        try {
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            i.printStackTrace();
            return false;
        }
    }
}
