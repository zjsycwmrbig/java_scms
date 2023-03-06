package scms.Dao;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
//重写FILE类更实用
/*
* @author zjs
* @function 简化文件操作
* @method new SCMSFILE(filepath) -- filepath是用户目录
*         未完待续
*
* */
public class SCMSFILE {
//    相对路径
    private String OS = "D:\\SCMSFILE\\";
    public File f;
//    构造函数
    public SCMSFILE(String pathname) {
        this.f = new File(OS + pathname);
    }
    public boolean exists(){
        if(this.f.exists()==true)return true;
        else return false;
    }
    public boolean createNewFile() throws IOException {
        if(this.f.createNewFile())return true;
        else return false;
    }
    public boolean mkdir(){
        if(this.f.mkdir())return true;
        else return false;
    }
}
