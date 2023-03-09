package scms.Dao;

import org.springframework.stereotype.Repository;
import scms.domain.ClassData;

/***
 * @author Administrator
 * @date 2023/3/8 15:29
 * @function
 */
//往
    @Repository
public class AddDao extends Dao{
<<<<<<< HEAD
    public boolean put(ClassData item,String user){
        scms = new SCMSFILE(user);
=======
    public boolean put(ClassData item,String className,String userName){
        scms = new SCMSFILE(className,userName);
>>>>>>> cc78721bc38c5e7cf83ccf486537dd69c210661c
        return true;
    }
}
