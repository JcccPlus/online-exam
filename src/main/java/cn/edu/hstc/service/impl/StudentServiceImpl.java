package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.StudentDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.service.StudentService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentDao studentDao;

    @Override
    public Student selectStudentById(Integer id) {
        return studentDao.selectStudentById(id);
    }

    @Override
    public Student findStudent(String stuNum, String password) {
        return studentDao.findStudent(stuNum, password);
    }

    @Override
    public List<Student> selectStudentList(Student student) {
        return studentDao.selectStudentList(student);
    }

    @Override
    public boolean insertStudent(Student student) {
        //生成32位学生唯一识别码
        String code = ProjectUtil.getUuid().substring(0, 16) + ProjectUtil.getMD5String(student.getStuNum()).substring(0, 16);
        student.setCode(code);
        //男女默认头像
        if ("男".equals(student.getGender())) {
            student.setHead_pic("/images/boy.png");
        } else if ("女".equals(student.getGender())) {
            student.setHead_pic("/images/girl.png");
        } else {
            return false;
        }
        //默认密码
        student.setPassword(student.getStuNum());
        return studentDao.insertStudent(student) > 0;
    }

    @Override
    @Transactional
    public AjaxResult addStudents(List<Student> students) {
        int rows = 0;
        try {
            for (Student student : students) {
                if (ObjectUtils.isEmpty(student.getClassId()) || student.getClassId() == 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("请选择班级");
                }
                if (ObjectUtils.isEmpty(student.getStuNum())) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("学号不为空且不能与已有学号重复");
                }
                if (ObjectUtils.isEmpty(student.getName())) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("请确认是否有姓名没有输入");
                }
                if (ObjectUtils.isEmpty(student.getGender())) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("性别不为空");
                } else {
                    if (!student.getGender().equals("男") && !student.getGender().equals("女")) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return AjaxResult.error("性别数据出错");
                    }
                }
                if (!ObjectUtils.isEmpty(student.getPhone()) && !ProjectUtil.isPhoneNum(student.getPhone())) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("请确认是否有手机号码不符合规范");
                }
                if (insertStudent(student)) {
                    rows += 1;
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("新增学生程序异常，请稍后重试！");
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("请确认是否有学号重复！");
        }
        return AjaxResult.success("成功添加" + rows + "条学生数据！");
    }

    @Override
    public boolean updateStudent(Student student) {
        return studentDao.updateStudent(student) > 0;
    }

    @Override
    public boolean deleteStudent(Student student) {
        return studentDao.deleteStudent(student) > 0;
    }

    @Override
    public List<Student> selectStudentsOfMissingExam(Exam exam) {
        return studentDao.selectStudentsOfMissingExam(exam);
    }

    @Override
    public AjaxResult updateHeadPic(Student student, MultipartFile file) {
        String des_path = null;  //头像文件文件夹地址
        try {
            des_path = URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "UTF-8") + "resources/head-pic/" + student.getStuNum();
            File dir = new File(des_path);
            if (!dir.exists()) {
                //没有就创建
                dir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();   //前端文件名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));  //文件格式
            String fileName = ProjectUtil.getMD5String(originalFilename) + ProjectUtil.getUuid().substring(0, 8) + suffix;   //最终保存的文件名(md5加密文件名+八位随机码+文件格式)
            File headFile = new File(dir, fileName);  //创建文件对象，表示要保存的头像文件,第一个参数表示存储的文件夹，第二个参数表示存储的文件名
            file.transferTo(headFile);  //执行保存
            String head_pic = "/head-pic/" + student.getStuNum() + "/" + fileName;  //新头像
            Student newStudent = new Student();
            newStudent.setId(student.getId());
            newStudent.setCode(student.getCode());
            newStudent.setHead_pic(head_pic);
            String old_head_pic = student.getHead_pic();  //旧头像
            if (updateStudent(newStudent)) {
                //更新用户session，更新页面头像显示
                student.setHead_pic(head_pic);
                ServletUtils.getSession().setAttribute("user", student);
                //删除服务器中的旧头像
                if (old_head_pic.contains(student.getStuNum())) {
                    File oldHeadFile = new File(dir, old_head_pic.substring(old_head_pic.lastIndexOf("/")));
                    oldHeadFile.delete();
                }
            } else {
                return AjaxResult.error("头像更新程序异常！请稍后重试！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("程序异常");
        }
        return AjaxResult.success("头像修改成功！");
    }

}
