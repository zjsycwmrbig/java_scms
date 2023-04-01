package scms.Dao;

import org.springframework.stereotype.Repository;
import scms.domain.GetJson.ClassData;

/***
 * @author Administrator
 * @date 2023/3/8 15:29
 * @function
 */
//往
    @Repository
public class AddDao extends Dao{
    public boolean put(ClassData item,String className,String userName){
        return true;
    }
}
