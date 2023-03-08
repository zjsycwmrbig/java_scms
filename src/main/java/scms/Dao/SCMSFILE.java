package scms.Dao;
import java.io.File;
import java.io.IOException;


//重写FILE类更实用
/*
* @author zjs
* @function 简化文件操作
* @method new SCMSFILE(filepath) -- filepath是用户目录
*         未完待续
* */


public class SCMSFILE {
//    相对路径
    private String OS = "D:\\SCMSFILE\\";
    public File father;
//静态文件夹
    public File assertdata;
//    课程文件
    public File classdata;
//    用户数据 - 名称,密码,注册时间
    public File userdata;
//    文件哈希
    public File hashdata;
//    构造函数
    public SCMSFILE(String pathname) {

        this.father = new File(OS + pathname);
        this.assertdata = new File(OS+pathname+"/assert");
        this.classdata = new File(OS+pathname+"/classdata.scms");
        this.userdata = new File(OS + pathname+"/userdata.scms");
        this.hashdata = new File(OS + pathname+"/hashdata.scms");
    }


    public boolean exists(){
        if(this.father.exists()==true)return true;
        else return false;
    }
    public  boolean creat() throws IOException {
        if(this.father.mkdirs()&&this.userdata.createNewFile()&&this.classdata.createNewFile()&&this.assertdata.mkdirs()){
            return true;
        }else{
            return false;
        }
    }
    public boolean mkdirs(){
        if(this.father.mkdirs())return true;
        else return false;
    }
}
