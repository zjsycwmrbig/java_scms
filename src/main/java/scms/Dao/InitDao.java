package scms.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import scms.domain.ClassData;
import scms.domain.HashData;

import java.io.IOException;

/***
 * @author Administrator
 * @date 2023/3/9 9:45
 * @function
 */
public class InitDao extends Dao{
    public ClassData ClassList[];
    public HashData HashList[];
    public boolean init(HttpSession session) throws IOException {

        JSON = new ObjectMapper();
//        通过session得到uername和classname session设置时长为永久,显然不合适
        scms = new SCMSFILE();

        HashList = JSON.readValue(scms.hashdata,HashData[].class);


//        保存到session
        session.setAttribute("ClassList",ClassList);
        session.setAttribute("HashList",HashList);
        return true;
    }
}
