package scms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import scms.Dao.DataProcessor;
import scms.Dao.DataRBTree;
import scms.Dao.DatabaseManager;
import scms.Service.OnlineManager;
import scms.domain.ServerJson.ClashTime;

import java.util.Date;

@SpringBootTest
class Demo1ApplicationTests {


    // 测试冲突时间二叉树查询算法
    @Test
    void FindEasyTime(){

        DatabaseManager.Init();
        DataProcessor data = OnlineManager.GetEventData("测试组织保存",0L);
        System.out.println(data.dataItem.name);
        System.out.println(data.dataItem.filePath.getAbsoluteFile());
        data.print();

        System.out.println("测试冲突组织在一天之后的所有空闲时间");
        ClashTime clash = data.FindEasyTime(new Date().getTime(),1);
        System.out.println(clash.begins.toString());
        System.out.println(clash.ends.toString());

        System.out.println("测试冲突组织在一周之后的所有空闲时间");
        ClashTime clash2 = data.FindEasyTime(new Date().getTime(),7);
        System.out.println(clash2.begins.toString());
        System.out.println(clash2.ends.toString());
    }
    // 测试通过
    @Test
    void SearchNeighbor(){
        // 新建一个DataRBTree
        DataRBTree dataRBTree = new DataRBTree();
        // 依次插入数据
        for(Long i = 0L;i < 333;i += 5){
            // i i+1 i+2
            Long random = (long)(Math.random() * 10) % 3; // 获得随机数
            for(int j = 0;j < 3;j++) {
                dataRBTree.AddItem(i + j, (long)(i + (j + random) % 3), i + j);
            }
        }

        DataProcessor dataProcessor = new DataProcessor();
        dataProcessor.dataRBTree = dataRBTree;
        dataProcessor.print();
        for(int i = 0;i < 100;i++){
            // 测试100次
            int random = (int)(Math.random() * 1000) % 333; // 获得随机数
            dataRBTree.searchNeibor(random);
            System.out.println(random);

            System.out.printf("左边:%d 右边:%d\n",dataRBTree.stack.get(0)!=null? dataRBTree.stack.get(0).key :-1,dataRBTree.stack.get(1)!=null?dataRBTree.stack.get(1).key:-1);
        }
    }

    // 测试冲突事件合并算法
    @Test
    void InterTime() {
        ClashTime time = new ClashTime();
        time.begins.clear();
        time.ends.clear();
        Long begin = 0L;
        for(int i = 0;i <= 5;i++){
             begin +=  (long) (Math.random() * 100);
            time.begins.add(begin);
            begin = begin + (long) (Math.random() * 1000);
            time.ends.add(begin);

        }
        ClashTime interTime = new ClashTime();
        System.out.println("合并前 时间A");
        System.out.println(time.begins.toString());
        System.out.println(time.ends.toString());
        interTime.begins.clear();
        interTime.ends.clear();
        begin = 0L;
        for(int i = 0;i <= 10;i++){
            begin +=  (long) (Math.random() * 100);
            interTime.begins.add(begin);
            begin = begin + (long) (Math.random() * 1000);
            interTime.ends.add(begin);
        }
        System.out.println("合并前 时间B");
        System.out.println(interTime.begins.toString());
        System.out.println(interTime.ends.toString());


        time.interSet(interTime);
        System.out.println("合并后的时间");
        System.out.println(time.begins.toString());
        System.out.println(time.ends.toString());
    }

}
