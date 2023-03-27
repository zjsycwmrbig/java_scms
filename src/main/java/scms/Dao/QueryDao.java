package scms.Dao;

import org.springframework.stereotype.Repository;
import scms.domain.ClassData;

import java.io.IOException;
import java.util.ArrayList;

/***
 * @author Administrator
 * @date 2023/3/15 15:42
 * @function
 */
@Repository
public class QueryDao extends Dao{
    InitDao merge = new InitDao();
    public ClassData[] QueryAll(String clas,String user) throws IOException {
        this.scms = new SCMSFILE(clas,user);
        ArrayList<ClassData> list = merge.mergeClass(this.scms);
        if(list == null) list = new ArrayList<ClassData>();
        return list.toArray(new ClassData[0]);
    }
}
