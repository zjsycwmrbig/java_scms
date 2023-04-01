package scms.Service;

import scms.Dao.DataProcessor;

import scms.Interceptor.BridgeData;
import scms.domain.GetJson.ClassData;

import scms.domain.ServerJson.UserFile;

import java.io.*;

import java.util.HashMap;
import java.util.List;


/***
 * @author Administrator
 * @date 2023/3/31 23:36
 * @function 处理多个DataProcess
 */
public class DataManager {
    public UserFile user;
//    给出该用户需要的处理器集合
    public HashMap<File,DataProcessor> owner;
    public HashMap<File,DataProcessor> player;

    public List<File> changeStack;      //改动栈

    public DataManager() {
        user = BridgeData.getRequestInfo();
        owner = new HashMap<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
                owner.put(user.owner.get(i),Fill(user.owner.get(i)));
            }
        }
        player = new HashMap<>();
        if(user.player!=null){
            for(int i = 0;i < user.player.size();i++){
                owner.put(user.player.get(i),Fill(user.player.get(i)));
            }
        }
    }

    public boolean Init(){//给出data的文件指针，找到位置
        user = BridgeData.getRequestInfo();
        owner = new HashMap<>();
        if(user.owner!=null){
            for(int i = 0;i < user.owner.size();i++){
                owner.put(user.owner.get(i),Fill(user.owner.get(i)));
            }
        }
        player = new HashMap<>();
        if(user.owner!=null){
            for(int i = 0;i < user.player.size();i++){
                owner.put(user.player.get(i),Fill(user.player.get(i)));
            }
        }
        if(owner == null) return false;

        return true;
    }
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
    public DataProcessor GetDataProcessor(File file){
        DataProcessor data = owner.get(file);
        if(data == null) return player.get(file);
        else return data;
    }


//    增
//    添加一个数据,校验一个
    public boolean AddItem(File file, ClassData item){
        DataProcessor data = owner.get(file);
        return data.AddItem(item);
    }


//  存储该用户的所有文件
    public boolean Save(){
        if(owner != null&& owner.size() != 0){
            for(int i = 0;i < owner.size();i++){
                if(!SaveItem(owner.get(user.owner.get(i)),user.owner.get(i))) return false;//一个个保存
            }
        }
        if(player != null && player.size() != 0){
            for(int i = 0 ;i < player.size();i++){
                if(!SaveItem(player.get(user.player.get(i)),user.player.get(i)))return false;//一个个保存
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
