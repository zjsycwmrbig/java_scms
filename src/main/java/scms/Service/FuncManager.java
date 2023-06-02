package scms.Service;

import org.springframework.stereotype.Service;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.GetJson.GetEventData;

/***
 * @author zjs
 * @date 2023/3/8 15:15
 * @function
 */

//判断这个数据可不可以加，包含排序,查找冲突等算法
@Service
public class FuncManager {
    // 自动生成口令
    static public String GenerateOrgPassword(){
        // 随机生成口令
        String[] passwords = {"床前明月光 疑是地上霜","仁者无敌","国脉所系 传邮万里","电梯要隔层按","神罗天征","地爆天星","急急如律令","快到碗里来","大本钟下寄快递","天下兴亡 匹夫有责","谁是我们的敌人?谁是我们的朋友?","我不做人啦","天予不取 必受其咎"};
        return passwords[(int)(Math.random() * passwords.length)];
    }

}
