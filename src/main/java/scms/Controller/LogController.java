package scms.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scms.Interceptor.BridgeData;
import scms.Interceptor.Log;
import scms.Interceptor.LogList;
import scms.Service.OnlineManager;
import scms.domain.ReturnJson.ReturnLogData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.util.List;

/**
 * @author seaside
 * 2023-05-25 21:32
 */

@RestController
@RequestMapping("/log")
public class LogController {
    @RequestMapping("/get")
    public void getLog(){
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        logList.read();
        List<Log> logs = logList.list;//得到Log数组
        //构造ReturnLogData,可能只需要Log里的时间和操作
    }
}
