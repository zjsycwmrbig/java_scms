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
    //红黑树文件
    public File rbtreeData;
    //构造函数
    public SCMSFILE(String className,String userName) {
        this.classDirectory = new File(OS + className);
        this.courseData = new File(OS+className+"/courseData.scms");
        this.studentDirectory = new File(OS + className +"/"+ userName);
        this.assertData = new File(OS+className +"/"+ userName + "/assert");
        this.userData = new File(OS + className +"/"+ userName + "/userData.scms");
        this.activityData = new File(OS + className +"/"+ userName + "/activityData.scms");
        this.hashData = new File(OS + className+"/"+ userName +"/hashdata.scms");
        this.rbtreeData = new File(OS + className+"/"+ userName +"/rbtreedata.scms");
    }

    public boolean exists(){
        return this.studentDirectory.exists();
    }
//    返回int值表示有没有班级被创建,0代表创建了班级,1代表没有创建班级,-1代表创建失败

//    搁置采用序列化更加方便
    public  int creat() throws IOException {
        if(!(this.classDirectory.exists())) {
//            班级文件不存在
            if (this.classDirectory.mkdirs()){
//            创建班级文件,
                if(!(this.courseData.createNewFile())) return 0;

            }else{
                return 0;
            }
        }
//        增加红黑树的文件
        if(!(this.studentDirectory.mkdirs()&&this.userData.createNewFile()&&this.assertData.mkdirs()&&this.hashData.createNewFile()&&this.rbtreeData.createNewFile()&&this.activityData.createNewFile())) return 0;//创建失败

        return 1;
    } //创建所有目录和文件
    public boolean mkdirs(){
        if(this.classDirectory.mkdirs())return true;
        else return false;
    }
}
