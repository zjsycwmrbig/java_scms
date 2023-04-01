package scms.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Dao {
    //文件指针,指向目录
    @Autowired
    ObjectMapper JSON;

}
