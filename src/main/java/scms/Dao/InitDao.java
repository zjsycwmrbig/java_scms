package scms.Dao;

import jakarta.servlet.http.HttpSession;
import scms.domain.ClassData;
import scms.domain.UserData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/***
 * @author Administrator
 * @date 2023/3/9 9:45
 * @function
 */

public class InitDao extends Dao{
//    一个session一个用户标识
    public boolean init(HttpSession session, UserData user) throws IOException {
//        通过session得到uername和classname session设置时长为永久,显然不合适

        scms = new SCMSFILE(user.getClassName(),user.getUsername());
//        保存到session
        ArrayList<ClassData> ClassList = mergeClass(scms);
        session.setAttribute("ClassList",ClassList);
        session.setAttribute("HashList",creatHash((ClassData[]) ClassList.toArray()));
        return true;
    }

    public ArrayList<ClassData> mergeClass(SCMSFILE scms) throws IOException {
//        JSON = new ObjectMapper();
        ArrayList<ClassData> ClassList = null;
        ClassData[] courseitem = JSON.readValue(scms.courseData, ClassData[].class);
        ClassData[] activityitem = JSON.readValue(scms.activityData, ClassData[].class);
        int length = courseitem.length + activityitem.length;
//      j 是 course的哨兵,k 是activity的哨兵
        for(int i = 0,j=0,k=0;i < length;i++){
            if(courseitem[j].begin < activityitem[k].begin){
//                debug
                if(j >= courseitem.length){
                    System.out.println("ERROR in mergeClass");
                }
                ClassList.add(courseitem[j]);
                j++;
            }else{
                ClassList.add(activityitem[k]);
                k++;
            }
        }
        return ClassList;
    }

// 创建map对应快速查找名称
    public Map<String, ArrayList<Integer>> creatHash(ClassData[] classList){
        Map<String,ArrayList<Integer>> HashMap = null;
        for (int i = 0;i < classList.length;i++){
            if(HashMap.containsKey(classList[i].title)){
                ArrayList<Integer> temp = HashMap.get(classList[i].title);
                temp.add(i);
                HashMap.put(classList[i].title,temp);
//                关闭temp
                temp.clone();
            }else{
                ArrayList<Integer> temp = null;
                temp.add(i);
                HashMap.put(classList[i].title,temp);
            }
        }
        return HashMap;
    }

}
