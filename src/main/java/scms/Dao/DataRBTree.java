package scms.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import scms.domain.ServerJson.ClashRBTNode;
import scms.domain.ServerJson.RBTNode;


import java.io.*;
import java.util.ArrayList;

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
    public long remove(long begin) {
        long res = 0;
        ClashRBTNode node = search(rbtree.Root,begin);
        if (node == null) return 0;
        res = (long) (node.vaule);
        rbtree.remove(node);// 直接删除这个节点
        return res;
    }
//   查
//    找到相邻节点
    public void searchNeibor(long inNode){
        // 清除stack缓存,初始化0,1位置
        stack.clear();
        stack.add(null);
        stack.add(null);
        // 找到某个节点左边的节点,右边的节点,仅仅通过判断的时候暂存是不够的
        // 可能会有两个节点不是父子关系可能,因此对于左侧和右侧的节点,需要特判
        searchNeibor(rbtree.Root,inNode);
        if(stack.get(0) == null){
            // 不存在左边界,说明这个节点是最小的节点,或者是一直向左找的,需要找到右节点的左侧最小值
            if(stack.get(1) == null){
                // 不存在右节点,说明这个树是空的
                return;
            }
            ClashRBTNode node = stack.get(1).left;
            while(node != null){
                stack.set(0, node);
                node = node.right;
            }
        }
    }
    //
    private void searchNeibor(ClashRBTNode x,long begin){
        if (x==null)
            return;
        int cmp = rbtree.Compare(x,begin);
        if (cmp >= 0){
            // 向左找,赋值右侧节点,注意这个可能会有一样的节点
            stack.set(1, x);
            searchNeibor(x.left, begin);
        }
        else if (cmp < 0){
            stack.set(0, x);
            searchNeibor(x.right, begin);
        }
    }

    public boolean search(long begin){
        ClashRBTNode node = search(rbtree.Root,begin);
        if (node == null) return false;
        return true;
    }
    // 普通查找
    private ClashRBTNode search(ClashRBTNode x,Long begin) {
//      返回最后的上级
        if (x==null) return null;
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
        if(root ==  null) {
            stack.clear(); // 暂存栈清零
            return;
        }
        //空就不找了
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
