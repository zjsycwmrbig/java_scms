package scms.Interceptor;

import scms.Service.OnlineManager;
import scms.domain.GetJson.GetUserData;
import scms.domain.ReturnJson.ReturnAddJson;
import scms.domain.ReturnJson.ReturnJson;
import scms.domain.ReturnJson.ReturnUserData;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author seaside
 * 2023-05-16 16:53
 */
public class WriteLog {
    //只记录成功的事件
    //若失败的也要记录，那就不需要returnJson了
    //todo:重复的代码可以抽取出来 ； 感觉取不同的名字太丑了，试试重载吧
    public static void writeAddLog(UserFile userFile, ReturnAddJson returnAddJson,String itemTitle, String methodName){
        if(!returnAddJson.res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username, operate+itemTitle);
        System.out.println("添加增加部分--"+operate+itemTitle+"--日志信息成功");
    }

    public static void writeDeleteLog(UserFile userFile,ReturnJson returnJson,String methodName){
        if(!returnJson.res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username,operate);
        System.out.println("添加删除部分--"+operate+"--日志信息成功");
    }

    public static void writeOrgLog(UserFile userFile,ReturnJson returnJson,String org,String methodName){
        if(!returnJson.res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username, operate+org);
        System.out.println("添加组织部分--"+operate+org+"--日志信息成功");
    }

    public static void writeQueryLog(UserFile userFile,boolean res,String methodName,String key){
        if(!res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username, operate+key);
        System.out.println("添加查询课程部分--"+operate+key+"--日志信息成功");
    }
    public static void writeUserLog(UserFile userFile,ReturnJson returnJson,String methodName){
        if(!returnJson.res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username,operate);
        System.out.println("添加登录部分--"+operate+"--日志信息成功");
    }

    public static String getTimeString(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");
        return dateTimeFormatter.format(localDateTime);
    }
}
