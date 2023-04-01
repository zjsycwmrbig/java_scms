package scms.domain.ServerJson;
import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/3/22 16:30
 * @function
 */
public class RBTNode<T,U> implements Serializable {
    public boolean color;   // 颜色
    public T vaule;  //vaule值

    public U key;   //开始
    public RBTNode  left;    // 左孩子
    public RBTNode right;    // 右孩子
    public RBTNode parent;    // 父结点

    public RBTNode(T vaule, U key,boolean color, RBTNode parent, RBTNode left, RBTNode right) {
        this.vaule = vaule;
        this.key = key;
        this.color = color;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }
}
