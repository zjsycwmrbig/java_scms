package scms.Service;

import scms.domain.ClassData;

/***
 * @author zjs
 * @date 2023/3/15 16:20
 * @function
 */

@org.springframework.stereotype.Service
public class Service {
//    在list中保证item前面的都是比item小的事件
    public int BinarySearch(ClassData[] list,ClassData item){
        int left = 0;
        int right = list.length - 1;//最右边的编号
        int mid;
        while(left < right){
            mid = (left + right)/2;
            if(list[mid].begin <= item.begin){
                left = mid+1;
            }else{
                right = mid-1;
            }
        }
        return left;
    }
}
