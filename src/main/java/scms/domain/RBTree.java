package scms.domain;

/***
 * @author Administrator
 * @date 2023/3/22 16:40
 * @function
 */
public class RBTree {
    private static final boolean RED   = false;
    private static final boolean BLACK = true;
    private RBTNode Root;    // 根结点
//  构造函数
    public RBTree() {
        Root =null;
    }
//  插入操作
    public void insert(RBTNode node) {
        int cmp;
        RBTNode y = null;
        RBTNode x = this.Root;

        // 1. 将红黑树当作一颗二叉查找树，将节点添加到二叉查找树中。
        while (x != null) {
            y = x;
            cmp = node.compareTo(x);
            if (cmp == 0)
                x = x.left;
            else
                x = x.right;
        }

        node.parent = y;
        if (y!=null) {
            cmp = node.compareTo(y);
            if (cmp == 0)
                y.left = node;
            else
                y.right = node;
        } else {
            this.Root = node;
        }

        // 2. 设置节点的颜色为红色
        node.color = RED;

        // 3. 将它重新修正为一颗二叉查找树
        insertFixUp(node);
    }
//  插入修正
    private void insertFixUp(RBTNode node) {
        RBTNode parent, gparent;

        // 若“父节点存在，并且父节点的颜色是红色”
        while (((parent = node.parent)!=null) && parent.color==false) {
            gparent = parent.parent;

            //若“父节点”是“祖父节点的左孩子”
            if (parent == gparent.left) {
                // Case 1条件：叔叔节点是红色
                RBTNode uncle = gparent.right;
                if ((uncle!=null) && uncle.color==false) {
                    uncle.color = BLACK;
                    parent.color = BLACK;
                    gparent.color = RED;
                    node = gparent;
                    continue;
                }

                // Case 2条件：叔叔是黑色，且当前节点是右孩子
                if (parent.right == node) {
                    RBTNode tmp;
                    leftRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // Case 3条件：叔叔是黑色，且当前节点是左孩子。
                parent.color = BLACK;
                gparent.color = RED;
                rightRotate(gparent);
            } else {    //若“z的父节点”是“z的祖父节点的右孩子”
                // Case 1条件：叔叔节点是红色
                RBTNode uncle = gparent.left;
                if ((uncle!=null) && uncle.color==false) {
                    uncle.color = BLACK;
                    parent.color = BLACK;
                    gparent.color = RED;
                    node = gparent;
                    continue;
                }

                // Case 2条件：叔叔是黑色，且当前节点是左孩子
                if (parent.left == node) {
                    RBTNode tmp;
                    rightRotate(parent);
                    tmp = parent;
                    parent = node;
                    node = tmp;
                }

                // Case 3条件：叔叔是黑色，且当前节点是右孩子。
                parent.color = BLACK;
                gparent.color = RED;
                leftRotate(gparent);
            }
        }

        // 将根节点设为黑色
        this.Root.color = BLACK;
    }
//  左旋
    private void leftRotate(RBTNode x) {
        // 设置x的右孩子为y
        RBTNode y = x.right;

        // 将 “y的左孩子” 设为 “x的右孩子”；
        // 如果y的左孩子非空，将 “x” 设为 “y的左孩子的父亲”
        x.right = y.left;
        if (y.left != null)
            y.left.parent = x;

        // 将 “x的父亲” 设为 “y的父亲”
        y.parent = x.parent;

        if (x.parent == null) {
            this.Root = y;            // 如果 “x的父亲” 是空节点，则将y设为根节点
        } else {
            if (x.parent.left == x)
                x.parent.left = y;    // 如果 x是它父节点的左孩子，则将y设为“x的父节点的左孩子”
            else
                x.parent.right = y;    // 如果 x是它父节点的左孩子，则将y设为“x的父节点的左孩子”
        }

        // 将 “x” 设为 “y的左孩子”
        y.left = x;
        // 将 “x的父节点” 设为 “y”
        x.parent = y;
    }
//  右旋
    private void rightRotate(RBTNode y) {
        // 设置x是当前节点的左孩子。
        RBTNode x = y.left;

        // 将 “x的右孩子” 设为 “y的左孩子”；
        // 如果"x的右孩子"不为空的话，将 “y” 设为 “x的右孩子的父亲”
        y.left = x.right;
        if (x.right != null)
            x.right.parent = y;

        // 将 “y的父亲” 设为 “x的父亲”
        x.parent = y.parent;

        if (y.parent == null) {
            this.Root = x;            // 如果 “y的父亲” 是空节点，则将x设为根节点
        } else {
            if (y == y.parent.right)
                y.parent.right = x;    // 如果 y是它父节点的右孩子，则将x设为“y的父节点的右孩子”
            else
                y.parent.left = x;    // (y是它父节点的左孩子) 将x设为“x的父节点的左孩子”
        }

        // 将 “y” 设为 “x的右孩子”
        x.right = y;

        // 将 “y的父节点” 设为 “x”
        y.parent = x;
    }
//  找到begin代表节点,删除它
    public void remove(long begin) {
        RBTNode node;

        if ((node = search(Root,begin)) != null)
            remove(node);// 直接删除这个节点
    }
//  移除以begin开始的节点
    private void remove(RBTNode node) {
        RBTNode child, parent;
        boolean color;

        // 被删除节点的"左右孩子都不为空"的情况。
        if ( (node.left!=null) && (node.right!=null) ) {
            // 被删节点的后继节点。(称为"取代节点")
            // 用它来取代"被删节点"的位置，然后再将"被删节点"去掉。
            RBTNode replace = node;

            // 获取后继节点
            replace = replace.right;
            while (replace.left != null)
                replace = replace.left;

            // "node节点"不是根节点(只有根节点不存在父节点)
            if (node.parent!=null) {
                if (node.parent.left == node)
                    node.parent.left = replace;
                else
                    node.parent.right = replace;
            } else {
                // "node节点"是根节点，更新根节点。
                this.Root = replace;
            }

            // child是"取代节点"的右孩子，也是需要"调整的节点"。
            // "取代节点"肯定不存在左孩子！因为它是一个后继节点。
            child = replace.right;
            parent = replace.parent;
            // 保存"取代节点"的颜色
            color = replace.color;

            // "被删除节点"是"它的后继节点的父节点"
            if (parent == node) {
                parent = replace;
            } else {
                // child不为空
                if (child!=null)
                    child.parent = parent;
                parent.left = child;

                replace.right = node.right;
                node.right.parent = replace;
            }

            replace.parent = node.parent;
            replace.color = node.color;
            replace.left = node.left;
            node.left.parent = replace;

            if (color == BLACK)
                removeFixUp(child, parent);

            node = null;
            return ;
        }

        if (node.left !=null) {
            child = node.left;
        } else {
            child = node.right;
        }

        parent = node.parent;
        // 保存"取代节点"的颜色
        color = node.color;

        if (child!=null)
            child.parent = parent;

        // "node节点"不是根节点
        if (parent!=null) {
            if (parent.left == node)
                parent.left = child;
            else
                parent.right = child;
        } else {
            this.Root = child;
        }

        if (color == BLACK)
            removeFixUp(child, parent);
        node = null;
    }
//   删除修正
    private void removeFixUp(RBTNode node, RBTNode parent) {
        RBTNode other;

        while ((node==null || node.color==BLACK) && (node != this.Root)) {
            if (parent.left == node) {
                other = parent.right;
                if (other.color==RED) {
                    // Case 1: x的兄弟w是红色的
                    other.color = BLACK;
                    parent.color = RED;
                    leftRotate(parent);
                    other = parent.right;
                }

                if ((other.left==null || other.left.color == BLACK) &&
                        (other.right==null || other.right.color==BLACK)) {
                    // Case 2: x的兄弟w是黑色，且w的俩个孩子也都是黑色的
                    other.color = RED;
                    node = parent;
                    parent = node.parent;
                } else {

                    if (other.right==null || other.right.color==BLACK) {
                        // Case 3: x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        other.left.color = BLACK;
                        other.color = RED;
                        rightRotate(other);
                        other = parent.right;
                    }
                    // Case 4: x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    other.color = parent.color;
                    parent.color = BLACK;
                    other.right.color = BLACK;
                    leftRotate(parent);
                    node = this.Root;
                    break;
                }
            } else {

                other = parent.left;
                if (other.color == RED) {
                    // Case 1: x的兄弟w是红色的
                    other.color = BLACK;
                    parent.color = RED;
                    rightRotate(parent);
                    other = parent.left;
                }

                if ((other.left==null || other.left.color == BLACK) &&
                        (other.right==null || other.right.color == BLACK)) {
                    // Case 2: x的兄弟w是黑色，且w的俩个孩子也都是黑色的setRed(other);
                    other.color = RED;
                    node = parent;
                    parent = node.parent;

                } else {

                    if (other.left==null || other.left.color==BLACK) {
                        // Case 3: x的兄弟w是黑色的，并且w的左孩子是红色，右孩子为黑色。
                        other.right.color = BLACK;
                        other.color = RED;
                        leftRotate(other);
                        other = parent.left;
                    }

                    // Case 4: x的兄弟w是黑色的；并且w的右孩子是红色的，左孩子任意颜色。
                    other.color = parent.color;
                    parent.color = BLACK;
                    other.left.color = BLACK;
                    rightRotate(parent);
                    node = this.Root;
                    break;
                }
            }
        }

        if (node!=null)
            node.color = BLACK;
    }
//  查找离着一个时间节点最近的事件节点
    private RBTNode search(RBTNode x,long begin) {
//      返回最后的上级
        if (x==null)
            return x.parent;

        if (x.begin > begin)
            return search(x.left, begin);
        else if (x.begin < begin)
            return search(x.right, begin);
        else
            return x; // 这个是找到的节点,如果没有的话应该是到null那里面
    }

    private void print(RBTNode tree, int id, int direction) {

        if(tree != null) {

            if(direction==0)    // tree是根节点
                System.out.printf("%2d(B) is root\n", tree.id);
            else                // tree是分支节点
                System.out.printf("%2d(%s) is %2d's %6s child\n", tree.id, tree.color==RED?"R":"B", id, direction==1?"right" : "left");

            print(tree.left, tree.id, -1);
            print(tree.right,tree.id,  1);
        }
    }

    public void print() {
        if (Root != null)
            print(Root, Root.id, 0);
    }


}
