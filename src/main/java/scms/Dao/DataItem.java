package scms.Dao;

import org.springframework.stereotype.Component;
import scms.domain.GetJson.GetEventData;
import scms.domain.ServerJson.RBTNode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/3/27 19:58
 * @function 针对一个数据二叉树,进行按照id的方式进行存储,高速排序
 * 关于序列化,我们最后自己保存处理一下
 */

@Component
//    分开维护
public class DataItem implements Serializable {
    private static final boolean BLACK = true;
    public List<Long> users; //用户组
    public String name;//数据名

    public int type;//组织类型
    public File filePath;//数据路径
    public String password;//口令
    public RBTree<GetEventData,Long> itemRbtree; //用classdata当做vaule,Long值当做key,Long直接记录Begin就好
//  增删改查

    public DataItem() {
        itemRbtree = new RBTree<>();
        users = new ArrayList<>();
    }

    public boolean AddItem(GetEventData item){
        itemRbtree.insert(new RBTNode(item,item.begin,BLACK,null,null,null));
//        这里其实还应该判断下是不是真的添加上了
        return true;
    }
//    删除
    // 删除成功返回title,失败返回null
    public String remove(Long begin){
            //找到对应的dataItem
            String res = null;
            RBTNode node = itemRbtree.searchNode(itemRbtree.Root,begin);
            if(node == null) return null;
            GetEventData data = (GetEventData) node.vaule;
            data.size--;
            if(data.size == 0) {
                res = ((GetEventData) (node.vaule)).title;
                itemRbtree.remove(node);
            }
        return res;
    }

//    查询 - 键值是begin的节点数据节点
    public GetEventData SearchItem(Long begin){
        //begin作为id
        RBTNode node = itemRbtree.searchNode(itemRbtree.Root,begin);
        if(node == null) return null;
        else return (GetEventData) node.vaule;
    }
}
