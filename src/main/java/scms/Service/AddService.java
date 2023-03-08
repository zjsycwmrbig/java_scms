package scms.Service;

import org.springframework.stereotype.Service;
import scms.domain.ClassData;

/***
 * @author Administrator
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class AddService {
    public boolean CheckLogic(ClassData item){
        return true;
    }
}
