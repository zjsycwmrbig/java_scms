package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.Interceptor.BridgeData;
import scms.domain.ServerJson.RBTNode;

import java.io.*;
import java.util.ArrayList;

@Component
public class DataRBTree implements Serializable{
    private static final boolean BLACK = true;
    @Autowired
    RBTree<Long,Long> rbtree;//指定节点类型,id是begin的值

    public DataRBTree() {
        rbtree = new RBTree<>();
        stack = new ArrayList<>();
    }

    public ArrayList<RBTNode> stack; //暂存栈
//    增
    public void AddItem(long id) {
        RBTNode node=new RBTNode(id,id,BLACK,null,null,null);
    // 如果新建结点失败，则返回。
        if (node != null) rbtree.insert(node);
    }

//   删
    public void remove(long begin) {
        RBTNode node;
        if ((node = search(rbtree.Root,begin)) != null) rbtree.remove(node);// 直接删除这个节点
    }
//   查
//    找到相邻节点
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
        int cmp = rbtree.Compare(x,begin);
        if (cmp == 1){
            // 向左找,赋值右侧节点,注意这个可能会有一样的节点
            stack.set(1, x);
            searchNeibor(x.left, begin);
        }
        else if (cmp == -1){
            stack.set(0, x);
            searchNeibor(x.right, begin);
        }
    }

    // 普通查找
    private RBTNode search(RBTNode x,long begin) {
//      返回最后的上级
        if (x==null)
            return x;
        int cmp = rbtree.Compare(x,begin);
        if (cmp == 1)
            return search(x.left, begin);
        else if (cmp == -1)
            return search(x.right, begin);
        else
            return x; // 这个是找到的节点,如果没有的话应该是到null那里面
    }

    // 打印这棵树



    //  中序查找 从start到end的节点
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
//    部分中序
    private void Between(RBTNode node, long start, long end) {
        // 限界
        if (node == null) {
            return;
        }
        // 找到在两者之间的节点并且加入
        Between(node.left, start, end);
        int cmpstart = rbtree.Compare(node,start);
        int cmpend  = rbtree.Compare(node,end);
        // 该节点满足条件
        if (cmpstart>=0 && cmpend<0) {
            stack.add(node);
        }
        Between(node.right, start, end);
    }
}
