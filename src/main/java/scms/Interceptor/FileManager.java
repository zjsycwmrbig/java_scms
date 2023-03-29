package scms.Interceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/29 16:55
 * @function
 */

//管理文件储存的静态类
public  class FileManager {
    private static File userfile = new File("D:\\SCMSFILE\\FileManager\\UserFile");
    private static File datafile = new File("D:\\SCMSFILE\\FileManager\\DataFile");

    private static int Size [] = {100,100,100}; // 0 : 100,1 : 100
    private static int GroupSize = 3;//分层尺寸限制

    private static File NextFile(File f,String arg){
        StringBuilder os = new StringBuilder(f.getAbsolutePath());
        os.append('/').append(arg);
        return new File(os.toString());
    }

    private File NewFile(File f,String name){

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
            if(length == 0){
                length = 1;
            }//判断没有的情况
            Path.add(length);
            temp = NextFile(temp,length.toString());
        }// 一共两位

        for(int i = Path.size() - 1;i>0;i--){
            Path.set(i, Path.get(i) +1);
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

    //  返回一个User文件指针
    public File AddUser(String name){
        return NewFile(userfile,name);
    }

//  返回一个Data文件指针
    public File AddData(String name){
        return NewFile(datafile,name);
    }


}
