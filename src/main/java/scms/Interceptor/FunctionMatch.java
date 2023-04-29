package scms.Interceptor;

/**
 * @author seaside
 * 2023-04-26 11:45
 */
public class FunctionMatch {
    public static String getFunctionString(String name){
        switch (name){
            case "CreatUser": return "注册";
            case "CheckLogin": return "登录";
            case "CreateGroup": return "创建组织";
            case "JoinGroup": return "加入组织";
            case "oneTarget": return "找寻一个目标点的最短路径";
            case "moreTargets": return "找寻多个目标点的最短路径";
            case "QueryNow": return "查询当前课表";
            case "QueryAll":return "查询所有课表";
            case "QueryKey":return "查询单节课";
        }
        return "找不到对应操作名称";
    }
}
