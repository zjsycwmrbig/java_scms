package scms.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import scms.Interceptor.BridgeData;
import scms.domain.ServerJson.Log;
import scms.domain.ServerJson.LogList;
import scms.Service.OnlineManager;
import scms.domain.ReturnJson.ReturnLogData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author seaside
 * 2023-05-25 21:32
 */

@RestController
@ResponseBody
@RequestMapping("/log")
public class LogController {
    @RequestMapping("/get")
    public ReturnLogData getLog(@RequestParam("currentPage") int currentPage, @RequestParam("pageSize") int pageSize){
        // 接收前端传来的currentPage和pageSize
        UserFile userFile = OnlineManager.GetUserData(BridgeData.getRequestInfo(),0L);
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        //读取log.scms
        logList.read();
        List<Log> logs = logList.list;//得到Log数组
        //分页 start - end 不包含end
        int start = (currentPage-1)*pageSize;
        int end = currentPage*pageSize;

        if(end>logs.size())
            end = logs.size();
        if (start>end)
            return new ReturnLogData(false,"日志不存在",logs.size(),null);
        System.out.println("from" + start + "to" + end);
        System.out.println("日志总数："+logs.size());
        ArrayList<Log> subList = new ArrayList<>(logs.subList(start,end));

        return new ReturnLogData(true,"查询成功",logs.size(),subList);
    }
}
