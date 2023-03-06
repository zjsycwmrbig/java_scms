package scms.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scms.domain.UserData;
import scms.Dao.UserDao;

import java.io.IOException;

@Service
public class UserService {
    @Autowired
    UserDao userDao;
    public int CheckLogin(UserData user){
        if(userDao.CheckData(user.username)){
            if(userDao.GetPassword(user.getUsername()).equals(user.getPassword())){
                return 1;
            }else{
                return 0;
            }
        }else {
//          代表失败
            return -1;
        }
    }
    public int CreatUser(UserData user) throws IOException {
        return userDao.CreatUser(user);
    }
}
