package scms.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import scms.domain.ClassData;

import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class AddService {
    public boolean CheckLogic(ClassData item, HttpSession session){
//        通过在session中的存储List判断这个item是否合法,确定是否加入

//        合法的话
        InsertItem(item,session);

        return true;
    }

    public boolean InsertItem(ClassData item,HttpSession session){
//        把item按照begin从小到大的顺序插入到session的ClassList和HashList里面
//        强转换,不排除有bug可能
        ArrayList<ClassData> ClassList = (ArrayList<ClassData>)(session.getAttribute("ClassList"));

        for(int i = 0;i <= ClassList.size();i++){
            if(ClassList.get(i).begin >= item.begin || i == ClassList.size()){
                ClassList.add(i,item);
                break;
            }
        }

        return true;
    }


}
