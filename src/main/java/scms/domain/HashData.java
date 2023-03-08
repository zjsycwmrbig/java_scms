package scms.domain;

import org.springframework.stereotype.Component;

/***
 * @author Administrator
 * @date 2023/3/8 15:22
 * @function
 */

//创建一个哈希对应数据
@Component
public class HashData {
//        这个条目中存放的名称
        public String title;
//        这个名称对应的id有哪些
        public int ids[];
}
