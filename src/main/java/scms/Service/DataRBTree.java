package scms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.Dao.RBTree;
import scms.Interceptor.BridgeData;
import scms.domain.RBTNode;

import java.io.*;
import java.util.ArrayList;

@Component
public class DataRBTree {
    private static final boolean RED   = false;
    private static final boolean BLACK = true;
    @Autowired
    RBTree rbtree;

    public ArrayList<RBTNode> stack; //暂存栈
//    这个地方不弄初始化也是为了以后集体活动增加
    public boolean Init(){
        stack = new ArrayList<>();
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BridgeData.getRequestInfo().rbtreeData));
            rbtree = (RBTree) ois.readObject();
            ois.close();
            return  true;
        } catch (Exception e) {
            rbtree = new RBTree();
            return false;
        }
    }
//    增
    public void AddItem(int id,long begin) {
        RBTNode node=new RBTNode(id,begin,BLACK,null,null,null);

    // 如果新建结点失败，则返回。
        if (node != null) rbtree.insert(node);
    }

//   删
    public void remove(long begin) {
        RBTNode node;
        if ((node = search(rbtree.Root,begin)) != null) rbtree.remove(node);// 直接删除这个节点
    }
//   查
    public void searchNeibor(long inNode){
        // 清除stack缓存,初始化0,1位置
        stack.clear();
        stack.add(rbtree.Root);
        stack.add(rbtree.Root);
        searchNeibor(rbtree.Root,inNode);
    }
    //
    private void searchNeibor(RBTNode x,long begin){
        if (x==null)
            return;

        if (x.key >= begin){
            // 向左找,赋值右侧节点,注意这个可能会有一样的节点
            stack.set(1, x);
            searchNeibor(x.left, begin);
        }
        else if (x.key < begin){
            stack.set(0, x);
            searchNeibor(x.right, begin);
        }
    }

    // 普通查找
    private RBTNode search(RBTNode x,long begin) {
//      返回最后的上级
        if (x==null)
            return x;

        if (x.key > begin)
            return search(x.left, begin);
        else if (x.key < begin)
            return search(x.right, begin);
        else
            return x; // 这个是找到的节点,如果没有的话应该是到null那里面
    }

    // 打印这棵树
    private void print(RBTNode tree, Long begin, int direction) {

        if(tree != null) {

            if(direction==0)    // tree是根节点
                System.out.printf("%2d(B) is root\n", tree.key);
            else                // tree是分支节点
                System.out.printf("%2d(%s) is %2d's %6s child\n", tree.key, tree.color==RED?"R":"B", begin, direction==1?"right" : "left");

            print(tree.left, tree.key, -1);
            print(tree.right,tree.key,  1);
        }
    }

    public void print() {
        if (rbtree.Root != null)
            print(rbtree.Root, rbtree.Root.key, 0);
    }


    //  从start到end的节点
    public void Between(long start, long end) {
        // 自己找到合适的root节点 - 这里找到root在start和end之间的节点
        RBTNode root = rbtree.Root;
        while(start > rbtree.Root.key || end < rbtree.Root.key){
            if(start > rbtree.Root.key){
                root = rbtree.Root.right;
            }else{
                root = rbtree.Root.left;
            }
        }
        // 现在的root必定在start和end之间
        stack.clear(); // 暂存栈清零
        Between(root, start, end);
    }

    private void Between(RBTNode node, long start, long end) {
        // 限界
        if (node == null) {
            return;
        }
        // 找到在两者之间的节点并且加入
        Between(node.left, start, end);
        // 该节点满足条件
        if (node.key >= start && node.key < end) {
            stack.add(node);
        }
        Between(node.right, start, end);
    }

    public boolean sava(){
        try {
            FileOutputStream fileOut = new FileOutputStream(BridgeData.getRequestInfo().rbtreeData);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(rbtree);
            out.close();
            fileOut.close();
            return true;
        } catch (IOException i) {
            return false;
        }
    }
}
