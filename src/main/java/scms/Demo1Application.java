package scms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scms.Dao.DatabaseRBTree;
import scms.Dao.UserRBTree;

@SpringBootApplication
public class Demo1Application {
    public static void main(String[] args) {
        UserRBTree.Init(); //读取二叉树
        DatabaseRBTree.Init();//读取数据库二叉树
        SpringApplication.run(Demo1Application.class, args);
    }
}
