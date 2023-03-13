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
    private int Day = 86400000;
//    判断两个时间节点,是最小单元判断是否有冲突 a在前 b在后

    //   判断两个事件是否存在冲突,理论上讲所有事件判断后即可判定,a是在之前的事件,b是之后的事件
    public boolean CheckItem(ClassData a,ClassData b){
//  把a贴近b的时间,检查两个时间点是否存在冲突

        int det = a.begin - b.begin;//时间差
        int circle = (a.circle * Day);
        int sin = det / circle;//一共相差多少周期
        a.begin += sin * circle;
//        现在的a是最贴近b的a
        if(a.begin + a.length > b.begin){
//                存在冲突
            return false;
        }else{
            return true;
        }
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
//              表示在index下标处的,为0就是该事件在队列最前方,其余的减一就是所找事件下标
                int index = 0;
                if(ClassTemp.begin >= ClassList.get(0).begin){
                    //二分找到在该节点begin之前的节点
                    int left = 0;
                    int right = length - 1;//最右边的编号
                    int mid;
                    while(left < right){
                        mid = (left + right)/2;
                        if(ClassList.get(mid).begin <= ClassTemp.begin){
                            left = mid+1;
                        }else{
                            right = mid-1;
                        }
                    }
                    index = left+1;
                }
                if(index!=0){
//                    可能存在冲突
                    if(!CheckItem(ClassList.get(index-1),ClassTemp)) return false;
                }
            }
        }
        InsertItem(item,session);
// 没有冲突,返回true
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
