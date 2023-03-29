package scms.Service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import scms.Dao.DataDao;
import scms.domain.ClashErrorData;
import scms.domain.ClassData;

import java.util.ArrayList;

/***
 * @author zjs
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class AddService extends scms.Service.Service {
//    可以使用Canlendar来判断
    DataDao dataDao;
    private int Day = 86400000;
//    判断两个时间节点,是最小单元判断是否有冲突 a在前 b在后

    //   判断两个事件是否存在冲突,理论上讲所有事件判断后即可判定,a是在之前的事件,b是之后的事件
//  返回在第几个周期后存在冲突,-1代表无冲突
    public int CheckItem(ClassData a,ClassData b){
//  把a贴近b的时间,检查两个时间点是否存在冲突

        long det = b.begin - a.begin;//时间差
        int circle = (a.circle * Day);
        int sin = (int)(det / circle);//一共相差多少周期
        a.begin += sin * circle;
//        现在的a是最贴近b的a
        if(a.begin + a.length > b.begin){
//                存在冲突
            return sin;
        }else{
            return -1;
        }
    }
//    判断一个事件和所有已经存在的事件是否有冲突
    public ClashErrorData CheckLogic(ClassData item, HttpSession session){
        ClashErrorData res = new ClashErrorData();
//        通过在session中的存储List判断这个item是否合法,确定是否加入
//        合法的话,插入到session里面,返回true
//        我们判断当下添加节点的时间冲突,可以通过前面时间的节点开始时间增加周期时间的倍数,以便贴合后面的时间节点
//        添加节点第一次之后的存在周期的时间节点,依次遍历每次周期的节点和之前所有的事件比较,一直到结束时间


        ArrayList<ClassData> ClassList;

        ClassList = (ArrayList<ClassData>)(session.getAttribute("ClassList"));

            //      数组长度
            int length = ClassList.size();
            ClassData ClassTemp = item;//替身 让其中的begin加加circle
//              依次遍历需要比对的时间节点
                for(;ClassTemp.begin < item.end;ClassTemp.begin += item.circle*Day){
//              表示在index下标处的,为0就是该事件在队列最前方,其余的减一就是所找事件下标
                    int index = 0;
//                    这里一个坑
                    if(length!=0 && ClassTemp.begin >= ClassList.get(0).begin){
                        //二分找到在该节点begin之前的节点,这里的二分可以自己抽离出来
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
                    if(index != 0){
//                    可能存在冲突
                        int ErrorCircle = CheckItem(ClassList.get(index-1),ClassTemp);
                        if(ErrorCircle != -1) {
                            res.state = false;
                            res.circle = ErrorCircle;
                            res.item = ClassList.get(index-1);
                            return res;
                        }
                    }

                    if(item.circle == 0 || length == 0){
                        break;
                    }
                }
        InsertItem(item,session);
// 没有冲突,返回true
        return res;
    }
//这个可以直接通过上面调用
    public boolean InsertItem(ClassData item,HttpSession session){
//        把item按照begin从小到大的顺序插入到session的ClassList和HashList里面
//        强转换,不排除有bug可能
        ArrayList<ClassData> ClassList;
        ClassList = (ArrayList<ClassData>)(session.getAttribute("ClassList"));
//        这里有可能会存在一个问题,那就是ClassList是null,所以我们应该在注册的时候就添加一个事件来保证这个List是存在的,length设置为0就好
        for(int i = 0;i < ClassList.size();i++){
            if(ClassList.get(i).begin >= item.begin){
                ClassList.add(i,item);
                for(int j = 0;j < ClassList.size();j++){
                    System.out.println(ClassList.get(j).title);
                }
                System.out.println("OVER!");
                return true;
            }
        }

//        插入到最后一个
        ClassList.add(ClassList.size(),item);
        for(int i = 0;i < ClassList.size();i++){
            System.out.println(ClassList.get(i));
        }
        return true;
    }
//  新添加的节点
    public  boolean AddItem(ClassData item){
        dataDao = new DataDao();
        dataDao.Init();
        dataDao.print();
        System.out.println("--------------更改-------------");
        boolean flag = dataDao.AddItem(item);
        dataDao.print();
        dataDao.Save();
//        这里没有记忆
        return flag;
    }
}
