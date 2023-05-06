package scms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import scms.Controller.NavigationController;
import scms.Dao.DatabaseRBTree;
import scms.Dao.UserRBTree;

import java.io.IOException;

@SpringBootApplication
public class Demo1Application {
    public static void main(String[] args) {
        UserRBTree.Init(); //读取二叉树
        DatabaseRBTree.Init();//读取数据库二叉树
        try {
            NavigationController.readMap();
        } catch (IOException e) {
            System.out.println("读取文件失败");
        }
        SpringApplication.run(Demo1Application.class, args);
    }
}
