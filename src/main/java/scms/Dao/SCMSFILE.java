package scms.Dao;
import scms.domain.ClassData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


//重写FILE类更实用
/*
* @author zjs
* @function 简化文件操作，不同文件指针指向不同的信息文件，要用信息文件的时候直接使用对应的文件指针
* @method new SCMSFILE(filepath) -- filepath是用户目录
*         未完待续
* */


public class SCMSFILE {
//    相对路径
    private static final String OS = "D:\\SCMSFILE\\";
    //班级文件夹
    public File classDirectory;
    //学生学号文件夹
    public File studentDirectory;
    //静态文件？？？？干嘛的
    public File assertData;
    // 课程文件，直接在班级文件夹下,所以班级文件夹存在的话课程文件一定存在
    public File courseData;
    //用户数据 - 名称,密码,注册时间 ,在学号文件夹下
    public File userData;
    //活动文件，在学号文件夹下
    public File activityData;
    //文件哈希？？？？干嘛的
    public File hashData;
    //构造函数
    public SCMSFILE(String className,String userName) {
        this.classDirectory = new File(OS + className);
        this.courseData = new File(OS+className+"/courseData.scms");
        this.studentDirectory = new File(OS + className +"/"+ userName);
        this.assertData = new File(OS+className +"/"+ userName + "/assert");
        this.userData = new File(OS + className +"/"+ userName + "/userData.scms");
        this.activityData = new File(OS + className +"/"+ userName + "/activityData.scms");
        this.hashData = new File(OS + className+"/"+ userName +"/hashdata.scms");
    }

    public boolean exists(){
        return this.studentDirectory.exists();
    }
//    返回int值表示有没有班级被创建,0代表创建了班级,1代表没有创建班级,-1代表创建失败
    public  int creat() throws IOException {
        if(this.classDirectory.exists()){
//            班级目录存在

            if(this.studentDirectory.mkdirs()&&this.userData.createNewFile()&&this.courseData.createNewFile()&&this.assertData.mkdirs())
                return 1;
            else
                return -1;
        }
        else {
            if(this.classDirectory.mkdirs()&&this.studentDirectory.mkdirs()&&this.userData.createNewFile()&&this.courseData.createNewFile()&&this.activityData.createNewFile()&&this.assertData.mkdirs()){
                return 0;
            }
            else{
                return -1;
            }
        }

    } //创建所有目录和文件
    public boolean mkdirs(){
        if(this.classDirectory.mkdirs())return true;
        else return false;
    }
}
