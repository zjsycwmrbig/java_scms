package scms.Dao;
import java.io.File;
import java.io.IOException;


//重写FILE类更实用
/*
* @author zjs
* @function 简化文件操作，不同文件指针指向不同的信息文件，要用信息文件的时候直接使用对应的文件指针
* @method new SCMSFILE(filepath) -- filepath是用户目录
*         未完待续
* */


public class SCMSFILE {
//    相对路径
    private String OS = "D:\\SCMSFILE\\";
    //班级文件夹
    public File classDirectory;

    //静态文件？？？？
    public File assertData;
    // 课程文件，直接在班级文件夹下

    //public File studentDirectory; 学生文件夹，不知道添了后其他代码哪里要改？？？？下面这个构造方法肯定要，那其他地方读删文件的地方应该也要
    public File courseData;
    //    用户数据 - 名称,密码,注册时间
    public File userData;
    //    文件哈希
    public File hashData;
    //    构造函数
    public SCMSFILE(String className,String userName) {
        //pathname传的是用户名
        this.classDirectory = new File(OS + className);
        this.assertData = new File(OS+className+"/assert");
        this.courseData = new File(OS+className+"课程文件名");
        this.userData = new File(OS + className+"/userdata.scms");
        this.hashData = new File(OS + className+"/hashdata.scms");
    }


    public boolean exists(){
        if(this.classDirectory.exists()==true)return true;
        else return false;
    }
    public  boolean creat() throws IOException {
        if(this.classDirectory.mkdirs()&&this.userData.createNewFile()&&this.courseData.createNewFile()&&this.assertData.mkdirs()){
            return true;
        }else{
            return false;
        }
    }
    public boolean mkdirs(){
        if(this.classDirectory.mkdirs())return true;
        else return false;
    }
}
