package scms.Dao;

import scms.Interceptor.FileManager;
import scms.domain.ServerJson.RBTNode;

import java.io.*;

/***
 * @author Administrator
 * @date 2023/3/30 9:47
 * @function 仅此一个用户红黑树,静态即可
 */
public class UserRBTree {
    private static final boolean BLACK = true;
    //先前创建好的
    static File userRbtreeData = new File("D:\\SCMSFILE\\UserData.scms");
    static RBTree<File,Long> rbTree; //红黑树

    public static boolean Init(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(userRbtreeData));
            rbTree = (RBTree<File,Long>) ois.readObject();
            ois.close();
            return  true;
        } catch (Exception e) {
            rbTree = new RBTree<File,Long>();
            return false;
        }
    }
//  寻找学号为key的文件
    public static File searchFile(long key){
        return searchFile(rbTree.Root,key);
    }
    private static File searchFile(RBTNode x, Long key){
        if (x==null)
            return null;
        int cmp = rbTree.Compare(x,key);
        if (cmp == 1)
            return searchFile(x.left, key);
        else if (cmp == -1)
            return searchFile(x.right, key);
        else
            return (File) (x.vaule);
    }

//  添加学号为key的文件
    public static File AddItem(long key){
        File file = FileManager.AddUser(String.valueOf(key));
        rbTree.insert(new RBTNode(file,key,BLACK,null,null,null));
        return file;
    }
//  删除学号为key的文件
    public static  void Remove(long key){
        rbTree.remove(rbTree.searchNode(rbTree.Root,key));
    }
    public static boolean sava(){
        try {
            FileOutputStream fileOut = new FileOutputStream(userRbtreeData);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(rbTree);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            return false;
        }
    }
}
