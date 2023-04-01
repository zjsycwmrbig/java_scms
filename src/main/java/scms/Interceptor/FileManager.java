package scms.Interceptor;

import scms.Service.UserManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/29 16:55
 * @function
 */

//管理文件储存的静态类,返回文件位置而已

public  class FileManager {
    private static File userfile = new File("D:\\SCMSFILE\\FileManager\\UserFile");
    private static File datafile = new File("D:\\SCMSFILE\\FileManager\\DataFile");

    private static int Size [] = {100,100,3}; // 0 : 100,1 : 100
    private static int GroupSize = 3;//分层尺寸限制

    private static File NextFile(File f,String arg){
        StringBuilder os = new StringBuilder(f.getAbsolutePath());
        os.append('/').append(arg);
        return new File(os.toString());
    }
//  根据目前的目录状态返回合适的文件指针
    private static File NewFile(File f,String name){
        List<Integer> Path = new ArrayList<>();
        File temp = f;
        for(int i = 0;i < GroupSize;i++){
            if(!temp.exists()){
                temp.mkdirs();//创建目录
            }
            Integer length = null;
            try {
                length = temp.list().length;
            }catch (Exception e){
                e.printStackTrace();
            }// 目前目录
//          当下目录是0的时候,设置成1
            if(length == 0){
                length = 1;
            }//判断没有的情况
            Path.add(length);
            temp = NextFile(temp,length.toString());
        }// 一共两位

        for(int i = Path.size() - 1;i>0;i--){
            if(i == Path.size()) Path.set(i, Path.get(i) +1);
            if(Path.get(i) > Size[i]){
                Path.set(i, Path.get(i) - Size[i]);
                Path.set(i-1,Path.get(i-1) + 1); //进位
            }
        }
        StringBuilder os = new StringBuilder(f.getAbsolutePath().toString());
        for (int i = 0;i < Path.size()-1;i++){
            os.append('/').append(Path.get(i));
        }
        os.append('/').append(name);//添加名字
        return new  File(os.toString());
    }

    //  返回一个User文件指针,创建一个user应有的文件
    public static File AddUser(String name){
        File file = NewFile(userfile,name);//获得应该的文件
        if(file.mkdirs()) {
            try{
                UserManager.GetUserFile(file).createNewFile();
                UserManager.GetImageFile(file).createNewFile();
                return file;
            }catch (Exception e){
                file.delete();//异常出现删除目录
                return null;
            }
        }//创建一个文件夹
        else return null;
    }

//  返回一个Data文件指针
    public static File AddData(String name){
        File file = NewFile(datafile,name);//获得应该的文件
        file.mkdirs();
        file = NextFile(file,"index.scms");//真正的文件
        try{
            file.createNewFile();
            return file;
        }catch (Exception e){
            file.delete();//异常出现删除目录
            return null;
        }
    }
}
