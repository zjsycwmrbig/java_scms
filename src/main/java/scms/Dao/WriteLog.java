package scms.Dao;

import scms.domain.ServerJson.LogList;
import scms.domain.ServerJson.UserFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author seaside
 * 2023-05-16 16:53
 */
public class WriteLog {
    public static void writeLog(UserFile userFile,boolean res,String methodName,String content){
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        String result = operate + content;
        if(res)
            result = result.concat("成功");
        else
            result = result.concat("失败");
        logList.write(getTimeString(), userFile.username,result);
        System.out.println("添加--"+result+"--日志信息成功");
    }
/*
    public static void writeAddLog(UserFile userFile,boolean res,String itemTitle, String methodName){
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username, operate+itemTitle+res);
        System.out.println("添加增加部分--"+operate+itemTitle+res+"--日志信息成功");
    }

    public static void writeDeleteLog(UserFile userFile,boolean res,String methodName){
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username,operate+res);
        System.out.println("添加删除部分--"+operate+res+"--日志信息成功");
    }

    public static void writeOrgLog(UserFile userFile,boolean res,String org,String methodName){
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        logList.write(getTimeString(), userFile.username, operate+org+res);
        System.out.println("添加组织部分--"+operate+org+res+"--日志信息成功");
    }

    public static void writeQueryLog(UserFile userFile,boolean res,String methodName,String key){
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName);
        if(key == null)
            key = "";
        logList.write(getTimeString(), userFile.username, operate+key+res);
        System.out.println("添加查询课程部分--"+operate+key+res+"--日志信息成功");
    }*/
    public static void writeUserLog(UserFile userFile,boolean res,String methodName,String content){
        //注册、登录失败说明用户有问题，就不需要记录在用户文件中了
        if(!res)
            return;
        File UserPath = userFile.file;
        String LogPath = UserPath.getAbsolutePath().concat("\\log.scms");
        LogList logList = new LogList(LogPath);
        String operate = FunctionMatch.getFunctionString(methodName) + content;
        logList.write(getTimeString(), userFile.username,operate);
        System.out.println("添加登录部分--"+operate+"--日志信息成功");
    }

    public static String getTimeString(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分ss秒");
        return dateTimeFormatter.format(localDateTime);
    }
}
