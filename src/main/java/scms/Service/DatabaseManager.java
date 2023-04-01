package scms.Service;

import scms.Dao.DatabaseRBTree;
import scms.domain.ReturnJson.ReturnEventData;

import java.io.File;

/***
 * @author Administrator
 * @date 2023/3/31 17:20
 * @function
 */
public class DatabaseManager {

    //添加一个名字是name的数据,并且返回文件的指针
    public static File AddData(String name){
        DatabaseRBTree.Init();
        File file = DatabaseRBTree.AddItem(name);
        DatabaseRBTree.sava();
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
