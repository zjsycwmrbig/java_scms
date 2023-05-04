package scms.Service;

import scms.Dao.DatabaseRBTree;

import java.io.File;

/***
 * @author Administrator
 * @date 2023/3/31 17:20
 * @function
 */
public class DatapageManager {

    //添加一个名字是name的数据,并且返回文件的指针
    public static File AddData(String name){
        File file = DatabaseRBTree.AddItem(name);

        return file;
    }

    //    删除名字是name的数据
    public static boolean RemoveData(File file){
        if(file.exists()){
            return file.delete();
        }
        return true;
    }

}
