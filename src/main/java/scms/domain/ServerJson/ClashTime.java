package scms.domain.ServerJson;


import java.util.ArrayList;
import java.util.List;

/***
 * @author Administrator
 * @date 2023/5/26 20:53
 * @function
 */
public class ClashTime {
    public ArrayList<Long> begins;
    public ArrayList<Long> ends;

    public ClashTime() {
        this.begins = new ArrayList<>();
        this.ends = new ArrayList<>();
        // 给前面冲突预留一个位置
        begins.add(-1L);
        ends.add(-1L);
    }

    public void normal(){
        this.begins = new ArrayList<>();
        this.ends = new ArrayList<>();
    }

    // 实现两个时间区间取交操作,只能更小,不能更大

    // 对begin截取end , 对end截取begin
    public boolean interSet(ClashTime other){
        if(other == null) return false;//为空
        if(other.begins.size() == 0 || this.begins.size() == 0){
            // 无法合并
            begins.clear();
            ends.clear();
            return true;
        }//为空
        List<Long> newBegin = new ArrayList<>();
        List<Long> newEnd = new ArrayList<>();

        // 开始序号
        int index = 0;
        // index是第一个大于other.begin的end序号
        // 合并开始
        for(int i = 0;i < other.begins.size();i++){
            // 依次找下去
            while(index < this.begins.size() && other.begins.get(i) >= this.ends.get(index)){
                // 序号自增
                index++;
            }
            // 如果index超出了范围,说明other的begin都比this的begin大,直接退出
            if(index >= this.begins.size()) {
                // 合并结束
                break;
            }

            // 更新index指向的begin和end,当不满足条件的时候,index++
            newBegin.add(Math.max(this.begins.get(index),other.begins.get(i)));
            newEnd.add(Math.min(this.ends.get(index),other.ends.get(i)));
        }
        // 合并结束
        this.begins = (ArrayList<Long>) newBegin;
        this.ends = (ArrayList<Long>) newEnd;
        return true;
    }
}
