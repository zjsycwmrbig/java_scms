package scms.domain.ServerJson;

import org.springframework.stereotype.Component;

import java.io.Serializable;

/***
 * @author Administrator
 * @date 2023/4/7 19:15
 * @function
 */
public class ClashRBTNode<T,U> implements Serializable{
    public boolean color;   // 颜色
    public T vaule;  //vaule值

    public U key;   //开始

    public U end;   //结束
    public ClashRBTNode left;    // 左孩子
    public ClashRBTNode right;    // 右孩子
    public ClashRBTNode parent;    // 父结点

    public ClashRBTNode(T vaule, U key, U end,boolean color, ClashRBTNode parent, ClashRBTNode left, ClashRBTNode right) {
        this.vaule = vaule;
        this.key = key;
        this.end = end;
        this.color = color;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }
}
