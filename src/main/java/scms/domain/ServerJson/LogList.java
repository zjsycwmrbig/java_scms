package scms.domain.ServerJson;


import scms.domain.ServerJson.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author seaside
 * 2023-05-06 22:28
 */
public class LogList {
    //使用该类的时候，先获取路径
    String Path;

    public LogList(String path) {
        Path = path;
    }
    public List<Log> list;
    //按理说向文件中存的时候不需要list？？？还是说有个读出的方法，读出的时候用list？这不是Controller的东西吗
    public void read() {
        try {
            FileInputStream fileInputStream = new FileInputStream(Path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            list = (ArrayList<Log>) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
            /*
            //序列化的时候是一个个Log对象往里存，而读取的时候是一个个引用对象被读出，即一次一个Log对象，所以得循环
            try {
                while(true){ //无法读取?????
                    Log log = (Log) objectInputStream.readObject();
                    list.add(log);
                }
            } catch (Exception e) {
                //读完就会出现异常。不使用使用ObjectInputStream自带的available()方法是因为
                //它返回的字节数并不一定是完整对象序列化数据的长度，因此无法准确地判断输入流中是否还有完整的对象序列化数据。
                //因此，在读取对象序列化数据时仍然使用异常处理以确保能够正确地判断输入流是否已经读取完毕。
                System.out.println("反序列化完毕");

            }finally {
                objectInputStream.close();
                fileInputStream.close();
            }*/
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void write(String localDateTime, long userName, String operate){
        Log log = new Log(localDateTime,userName,operate);
        read();
        if(list == null)
            list = new ArrayList<>();
        list.add(log);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Path);
            ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
            outputStream.writeObject(list);
            outputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }//先将文件反序列化到list中，再添加一个Log对象到list，再将list序列化到文件中

}

