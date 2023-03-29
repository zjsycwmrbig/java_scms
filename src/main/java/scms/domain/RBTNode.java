package scms.domain;
import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/3/22 16:30
 * @function
 */
public class RBTNode implements Comparable<RBTNode>, Serializable {
    public boolean color;   // 颜色
    public int id;  //id

    public long key;   //开始
    public RBTNode  left;    // 左孩子
    public RBTNode right;    // 右孩子
    public RBTNode parent;    // 父结点

    public RBTNode(int id, long begin,boolean color, RBTNode parent, RBTNode left, RBTNode right) {
        this.id = id;
        this.key = begin;
        this.color = color;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(RBTNode o) {
        if(this.key > o.key)return 1;
        else return 0;
    }
}
