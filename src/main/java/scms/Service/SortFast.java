package scms.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author seaside
 * 2023-04-28 16:19
 */
public class SortFast {
    //静态方法，Comparator需要在调用该方法的地方针对T创建一个内部类
    public static <T> void fun(List<T> list, Comparator<T> c){
        int length = list.size();
        int lowPointer = 0;
        int highPointer = length - 1;
        SortFast.fun(list,c,lowPointer,highPointer);
    }

    public static <T> void fun(List<T> list, Comparator<T> c ,int lowPointer, int highPointer){
        int low = lowPointer;
        int high = highPointer;
        if(lowPointer >= highPointer){
            return;
        }
        T key = list.get(lowPointer);
        while(lowPointer < highPointer){
            while(true) {
                if(highPointer <= lowPointer)
                    break;
                if (c.compare(key, list.get(highPointer)) > 0) { //排序结果应该是从小到大，以结果为准
                    list.set(lowPointer, list.get(highPointer));
                    break;
                } else {
                    highPointer--;
                }
            }
            while(true){
                if(highPointer <= lowPointer)
                    break;
                if(c.compare(list.get(lowPointer),key) > 0){
                    list.set(highPointer,list.get(lowPointer));
                    break;
                }
                else{
                    lowPointer++;
                }
            }
        }
        list.set(lowPointer,key);
        SortFast.fun(list,c,low,lowPointer-1);
        SortFast.fun(list,c,lowPointer+1,high);
    }
}
