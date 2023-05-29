package scms;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;
import scms.Dao.DatabaseManager;
import scms.Dao.UserRBTree;
import scms.Service.OnlineManager;

/***
 * @author Administrator
 * @date 2023/5/2 7:42
 * @function 在服务器关闭的时候自动保存cache数据,达到保护数据的作用
 */
@Configuration
public class AppListen implements DisposableBean{
    @Override
    public void destroy() throws Exception {
        OnlineManager.Clear();//清除所有的在线缓存,保存到数据库
        System.out.println("缓存树数据保存");
        if(UserRBTree.empty()) System.out.println("用户树为空");
        else UserRBTree.sava();
        System.out.println("用户树数据保存");
        if(DatabaseManager.empty()) System.out.println("数据库树为空");
        else DatabaseManager.sava();
        System.out.println("数据库树数据保存");
    }
}
