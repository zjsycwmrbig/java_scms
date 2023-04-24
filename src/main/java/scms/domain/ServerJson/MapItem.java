package scms.domain.ServerJson;

/***
 * @author Administrator
 * @date 2023/4/24 9:49
 * @function 字符树的节点
 */
public class MapItem {
    public MapItem(Long id, Character next) {
        this.id = id;
        this.next = next;
    }

    public Long id;
    public Character next;//下一个字符
}
