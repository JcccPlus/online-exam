package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.StudentDao;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.service.StudentService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
