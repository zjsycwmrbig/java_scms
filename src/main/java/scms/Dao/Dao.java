package scms.Dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class Dao {
    //文件指针,指向目录
    SCMSFILE scms;      //通过session赋值,通过controller拦截request后给Dao赋值
    @Autowired
    ObjectMapper JSON;

}
