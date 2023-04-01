package scms.Service;

import org.springframework.stereotype.Service;
/***
 * @author zjs
 * @date 2023/3/15 15:06
 * @function
 */
@Service
public class QueryService extends scms.Service.Service {
    private int DAY = 1000 * 60 * 60 * 24;

//    返回当下所有的文件


//    public ClassData[] QueryNow(Date date,HttpSession session){
//        Calendar now = Calendar.getInstance();
////        获得当下事件
//        now.setTime(date);
//        // 获得当前日期是本周的第几天（1代表周日，2代表周一，以此类推）
//        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
//
//        // 计算本周的周一和周末的日期
//        now.add(Calendar.DATE, -dayOfWeek + 2); // 本周的周一
//        long monday = now.getTime().getTime();
//        now.add(Calendar.DATE, 7); // 下周的周一
//        long sunday = now.getTime().getTime();
////        筛选monday到sunday的数据
//        ArrayList<ClassData> arraylist = (ArrayList<ClassData>)session.getAttribute("ClassList");
//        ClassData[] list = arraylist.toArray(new ClassData[arraylist.size()]);
//        QueryTimeBetween(list,monday,sunday);
//        return list;
//    }
////      筛选出从...到...的数据,为什么要返回数组?更简练
//    public ClassData[][] QueryTimeBetween(ClassData[] list,long begin,long end){
////        初始化带星期的数组
//        ArrayList<ArrayList<ClassData>> List = new ArrayList<>();
//        for(int i = 0;i < 7;i++){
//            List.add(new ArrayList<ClassData>());
//        }
//        ArrayList<ClassData> temp1 = new ArrayList<>();
////        1.二分找到在end之前开始的数据,前面的数据都有可能
//        ClassData enditem = new ClassData();
//        enditem.begin = end;
//        int endid = this.BinarySearch(list,enditem);
////        2.通过每一条数据end时间判断,如果它的end时间早于begin,则不可能,如果它的circle是0并且begin不在里面,不可能
//        for(int i = 0;i < endid;i++){
//            if(list[i].end > begin ){
//                if(list[i].circle != 0){
//                    temp1.add(list[i]);
//                }else if(list[i].circle == 0 && list[i].begin >= begin){
////                    直接满足条件的单次条目,找到属于哪一个星期
//                    int index = (int)((list[i].begin - begin)/(1000 * 60 * 60 * 24));
//                    List.get(index).add(list[i]);
//                }
//            }
//        }
////        3.现在每条数据都有可能在begin和end之间,并且存在一个或者多个
////        4.通过begin - 每条数据的begin得到了这周开始和这条数据注册时间的差值
////        5.对差值取模得到这条数据距离begin时间最短的天数,取负数即是该数据在这周的星期数index
////        6.利用for循环 让index在小于7的循环中依次加circle,得到的就是在这周的星期数标号
////        7.对于每条数据插入到对应的数组中即可
//        for(int i = 0;i < temp1.size();i++){
////            circle 是 1 的时候,一天一次
//            if(temp1.get(i).circle == 1){
//                for(int index = 0;temp1.get(i).begin + index * DAY < temp1.get(i).end;index++){
//                    List.get(index).add(temp1.get(i));
//                }
//            }else{
//                long diff = begin - temp1.get(i).begin;
//                // 计算相差的天数
//                long days = diff / DAY;
//
//                int index = -(int)(days / temp1.get(i).circle);
////                第一个可能的是这样
//                index += temp1.get(i).circle;
//                for(;temp1.get(i).begin + index * DAY < temp1.get(i).circle;index+=temp1.get(i).circle){
//                    List.get(index).add(temp1.get(i));
//                }
//            }
//        }
////        这里的ClassData转换成二维数组需要再想一下
//        return (ClassData[][]) List.toArray();
//    }
}
