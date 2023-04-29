package scms.Service;

import java.util.List;

/**
 * @author seaside
 * 2023-04-28 16:13
 */
public class KMP {
    //根据字符串创建next[]数组
    public static int getKMPIndex(String main,String son){
        int index = -1;
        char[] mainChars = main.toCharArray();
        char[] sonChars = son.toCharArray();
        int mainPointer; //指向主串
        int sonPointer; //指向字串
        int mainLength = main.length();
        int sonLength = son.length();
        //得到next数组
        int[] next = new int[sonLength];
        next[0] = -1;
        int sonMainPointer = 0; //指向模式串
        sonPointer = -1; //指向模式串的逻辑子串
        while(sonMainPointer<sonLength-1){
            if(sonPointer == -1 || sonChars[sonMainPointer] == sonChars[sonPointer]){
                ++sonMainPointer;
                ++sonPointer;
                if(sonChars[sonMainPointer] != sonChars[sonPointer]){
                    next[sonMainPointer] = sonPointer;
                }
                else{
                    next[sonMainPointer] = next[sonPointer] + 1;
                }
            }
            else{
                sonPointer = next[sonPointer];
            }
        }
        //查找位置
        mainPointer = 0; //指向主串
        sonPointer = 0; //指向模式串
        while(mainPointer<mainLength && sonPointer<sonLength){
            if(sonPointer == -1 || mainChars[mainPointer] == sonChars[sonPointer]){
                ++sonPointer;
                ++mainPointer;
            }
            else{
                sonPointer = next[sonPointer];
            }
        }
        if(sonPointer >= sonLength)
            index = mainPointer - sonLength;
        return index;
    }
}
