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
//    可以使用Canlendar来判断
    public static int SEC = 1000;
    public static int MINUTE = 1000;
    public static int HOUR = 1000;
//    判断两个时间节点,是最小单元判断是否有冲突 a在前 b在后
    public boolean CheckPoint(ClassData a,ClassData b){
//        后期优化掉
        if(a.begin > b.begin){
            ClassData temp = a;
            a = b;
            b = temp;
        }

//        保持a在前,b在后
        if(a.begin + a.length > b.begin){
//                存在冲突
            return false;
        }else{
            return true;
        }
    }

    //   判断两个事件是否存在冲突,理论上讲所有事件判断后即可判定
    public boolean CheckItem(ClassData a,ClassData b){
        if(a.begin > b.begin){
            ClassData temp = a;
            a = b;
            b = temp;
        }

        return true;
    }
//    判断一个事件和所有已经存在的事件是否有冲突
    public boolean CheckLogic(ClassData item, HttpSession session){
//        通过在session中的存储List判断这个item是否合法,确定是否加入

//        合法的话,插入到session里面,返回true


//        我们判断当下添加节点的时间冲突,可以通过前面时间的节点开始时间增加周期时间的倍数,以便贴合后面的时间节点
//        添加节点第一次之后的存在周期的时间节点,依次遍历每次周期的节点和之前所有的事件比较,一直到结束时间
        ArrayList<ClassData> ClassList;
        ClassList = (ArrayList<ClassData>)(session.getAttribute("ClassList"));
//      数组长度
        int length = ClassList.size();
        boolean flag = true;
        if(item.circle != 0){
            ClassData ClassTemp = item;//替身 让其中的begin加加circle
//      依次遍历需要比对的时间节点

            for(;ClassTemp.begin < item.end;ClassTemp.begin += item.circle){
//                二分找到在该节点begin之前的节点
                int left = 0;
                int right = length - 1;//最右边的编号
                int mid;
                while(left <= right){
                    mid = (left + right)/2;
                    if(ClassList.get(mid).begin > ClassTemp.begin){
                        left = mid;
                    }else{
                        right = mid;
                    }
                }
            }
        }
        InsertItem(item,session);

        return true;
    }

    public boolean InsertItem(ClassData item,HttpSession session){
//        把item按照begin从小到大的顺序插入到session的ClassList和HashList里面
//        强转换,不排除有bug可能
        ArrayList<ClassData> ClassList;
        ClassList = (ArrayList<ClassData>)(session.getAttribute("ClassList"));

        for(int i = 0;i <= ClassList.size();i++){
            if(ClassList.get(i).begin >= item.begin || i == ClassList.size()){
                ClassList.add(i,item);
                break;
            }
        }

        return true;
    }


}
