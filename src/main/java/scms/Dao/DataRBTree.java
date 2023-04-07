package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.domain.ServerJson.ClashRBTNode;
import scms.domain.ServerJson.RBTNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataRBTree implements Serializable{
    private static final boolean BLACK = true;

    @Autowired
    ClashRBTree<Long,Long> rbtree;//指定节点类型,id是begin的值

    public DataRBTree() {
        rbtree = new ClashRBTree<>();
        stack = new ArrayList<>();
    }

    public ArrayList<ClashRBTNode> stack; //暂存栈
//    增
    public void AddItem(long id,long key,long end) {
        ClashRBTNode node=new ClashRBTNode(id,key,end,BLACK,null,null,null);
    // 如果新建结点失败，则返回。
        if (node != null) rbtree.insert(node);
    }

//   删
    public void remove(long begin) {
        ClashRBTNode node;
        if ((node = search(rbtree.Root,begin)) != null) rbtree.remove(node);// 直接删除这个节点
    }
//   查
//    找到相邻节点
    public void searchNeibor(long inNode){
        // 清除stack缓存,初始化0,1位置
        stack.clear();
        stack.add(null);
        stack.add(null);
        searchNeibor(rbtree.Root,inNode);
    }
    //
    private void searchNeibor(ClashRBTNode x,long begin){
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
    private ClashRBTNode search(ClashRBTNode x,long begin) {
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
        ClashRBTNode root = rbtree.Root;
        if(root ==  null)return;//空就不找了
        int cmpstart = rbtree.Compare(rbtree.Root,start);
        int cmpend = rbtree.Compare(rbtree.Root,end);
        while(cmpstart < -1 || cmpend > 1){
            cmpstart = rbtree.Compare(rbtree.Root,start);
            cmpend = rbtree.Compare(rbtree.Root,end);
            if(cmpstart < 0){
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
    private void Between(ClashRBTNode node, long start, long end) {
        // 限界
        if (node == null) {
            return;
        }
        // 找到在两者之间的节点并且加入
        Between(node.left, start, end);
        int cmpstart = rbtree.Compare(node,start);
        int cmpend  = rbtree.Compare(node,end);
        // 该节点满足条件
        if (cmpstart>=0 && cmpend<=0) {
            stack.add(node);
        }
        Between(node.right, start, end);
    }
}
