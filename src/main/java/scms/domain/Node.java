package scms.domain;

import java.util.Date;

/***
 * @author zjs
 * @date 2023/3/20 17:20
 * @function
 */

public class Node {
//    指向课程信息的cid这里应该默认是index
    public int cid;
    public long begin;

    public long length;
//  指向另外两个节点
    public Node left;
    public Node right;

    public Node(int cid,long begin,long length) {
        this.cid = cid;
        this.begin = begin;
        this.length = length;
    }
}
