package scms;

import ch.qos.logback.core.sift.AppenderFactoryUsingSiftModel;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import scms.Dao.DataProcessor;
import scms.Dao.DataRBTree;
import scms.Dao.DatabaseManager;
import scms.Dao.UserRBTree;
import scms.Service.OnlineManager;
import scms.Service.UserManager;
import scms.domain.ServerJson.ClashTime;
import scms.domain.ServerJson.UserFile;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@SpringBootTest
class Demo1ApplicationTests {
    static class Location {
        public Integer pid;
        public String name;
        public Double x;
        public Double y;
        public boolean type;
    }

    @Test
    void OrgCreate(){
        DatabaseManager.Init();
        UserRBTree.Init();

        System.out.println(DatabaseManager.searchFile("2班").getAbsolutePath());
        DataProcessor dataProcessor = OnlineManager.GetEventData("2班",0L);
        System.out.println(dataProcessor.dataItem.filePath.getAbsolutePath());

    }
    @Test
    void AlarmExist() {
        UserRBTree.Init();
        DatabaseManager.Init();
        UserFile user = OnlineManager.GetUserData(2021211202L,0L);
        System.out.println("用户名");
        System.out.println(user.username);
        System.out.println(user.Exist(1685289600000L));

    }




    // 测试冲突时间二叉树查询算法
    @Test
    void FindEasyTime(){

        DatabaseManager.Init();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        DataProcessor data = OnlineManager.GetEventData("测试完全空闲时间",0L);

        // 将时分秒,毫秒域清零
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        System.out.println("测试冲突组织在一天之后的所有空闲时间");
        ClashTime clash = data.FindEasyTime(calendar.getTimeInMillis(),1);
        // 打印结果
        for(int i = 0;i < clash.begins.size();i++){
            System.out.printf("%s %s\n",new Date(clash.begins.get(i)).toString(),new Date(clash.ends.get(i)).toString());
        }

        System.out.println("测试冲突组织在一周之后的所有空闲时间");
        // 更新出开始时间

        int dayOfWeek = (calendar.get(Calendar.DAY_OF_WEEK) + 5)%7;
        // 计算本周的周一和周末的日期
        calendar.add(Calendar.DATE, -dayOfWeek); // 本周的周一
        ClashTime clash2 = data.FindEasyTime(calendar.getTimeInMillis(),7);
        // 打印结果
        for(int i = 0;i < clash2.begins.size();i++){
            System.out.printf("%s %s\n",new Date(clash2.begins.get(i)).toString(),new Date(clash2.ends.get(i)).toString());
        }
        DatabaseManager.sava();
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
//                dataRBTree.AddItem(i + j, (long)(i + (j + random) % 3), i + j);
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

    // 构建locationMap
    @Test
    void CreateLocateMap() {
        // 读取文件,转换成locationMap

        // 使用JsonSon读取文件中的对象
        HashMap<String, Integer> locationMap = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Location[] locations = objectMapper.readValue(new File("D://Points.json"), Location[].class);
            for (Location location : locations) {
                if (!location.type){
                    locationMap.put(location.name, location.pid);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 将locationMap序列化
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D://locationMap.scms");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(locationMap);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream fileInputStream = new FileInputStream("D://locationMap.scms");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            HashMap<String, Integer> locationMap1 = (HashMap<String, Integer>) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
            System.out.println("反序列化结果");
            for(String key : locationMap1.keySet()){
                System.out.printf("%s %d\n",key,locationMap1.get(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
