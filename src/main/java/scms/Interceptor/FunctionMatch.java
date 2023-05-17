package scms.Interceptor;

/**
 * @author seaside
 * 2023-04-26 11:45
 */
public class FunctionMatch {
    public static String getFunctionString(String name){
        switch (name){
            case "CreatUser": return "注册成功";
            case "CheckLogin": return "登录成功";
            case "LogOut":return "退出";
            case "AddItem": return "添加数据信息";
            case "DeleteItem":return "删除数据信息";
            case "NoticeProcessor": return "同意通知";
            case "NoticeRemover":return "拒绝通知";
            case "NoticeIgnorer":return "忽略通知";
            case "NoticePolling":return "轮询获取通知";
            case "OrgCreate": return "创建组织";
            case "OrgJoin": return "加入组织";
            case "OrgInvite":return "邀请他人加入组织";
            case "OrgDelete":return "删除组织";
            case "oneTarget": return "找寻一个目标点的最短路径";
            case "moreTargets": return "找寻多个目标点的最短路径";
            case "QueryNow": return "查询当前课表";
            case "QueryAll":return "查询所有课表";
            case "QueryKey":return "查询单节课";
        }//返回的值对应接口
        return name; //找不到就原样返回，如没有调用接口的需要记录的方法
    }
}
