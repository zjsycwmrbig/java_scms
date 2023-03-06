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
    public int CheckLogin(UserData user) throws IOException {
        return userDao.CheckLogin(user);
    }
    public int CreatUser(UserData user) throws IOException {
        return userDao.CreatUser(user);
    }
}
