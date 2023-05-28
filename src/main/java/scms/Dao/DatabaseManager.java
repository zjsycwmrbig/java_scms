package scms.Dao;

import scms.Interceptor.FileManager;
import scms.domain.ServerJson.RBTNode;

import java.io.*;

/***
 * @author Administrator
 * @date 2023/3/31 18:32
 * @function 根据String提供的String值搜索数据文件夹
 */
public class DatabaseManager {
        private static final boolean BLACK = true;
        //先前创建好的
        static File DatabaseRbtreeData = new File("D:\\SCMSFILE\\Database.scms");
        static RBTree<File,String> rbTree; //红黑树

        public static boolean Init(){
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DatabaseRbtreeData));
                rbTree = (RBTree<File,String>) ois.readObject();
                ois.close();
                return  true;
            } catch (Exception e) {
                rbTree = new RBTree<File,String>();
                return false;
            }
        }

        //  寻找学号为key的文件
        public static File searchFile(String key){
            return searchFile(rbTree.Root,key);
        }

        private static File searchFile(RBTNode x, String key){
            //这里的key是一个字符串
            if (x == null) return null;
            int cmp = rbTree.Compare(x,key);
            if (cmp > 0)
                return searchFile(x.left, key);
            else if (cmp < 0)
                return searchFile(x.right, key);
            else
                return (File) (x.vaule);
        }

        public static  RBTNode<File,String> searchNode(RBTNode x,String key){
            if (x==null)
                return null;
            int cmp = rbTree.Compare(x,key);
            if (cmp > 0)
                return searchNode(x.left, key);
            else if (cmp < 0)
                return searchNode(x.right, key);
            else
                return x;
        }

        //  添加学号为key的文件
        public static File AddItem(String key){
            File searchRes =  searchFile(key);
            if (searchRes != null) return null; //已经存在了
            File file = FileManager.AddData(key);
            rbTree.insert(new RBTNode(file,key,BLACK,null,null,null));
            return file;
        }

        //  删除学号为key的文件
        public static  void Remove(String key){
            rbTree.remove(searchNode(rbTree.Root,key));
        }

        public static boolean sava(){
            try {
                FileOutputStream fileOut = new FileOutputStream(DatabaseRbtreeData);
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
