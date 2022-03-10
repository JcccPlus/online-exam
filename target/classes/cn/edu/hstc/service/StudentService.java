package cn.edu.hstc.service;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    Student selectStudentById(Integer id);

    Student findStudent(String stuNum, String password);

    List<Student> selectStudentList(Student student);

    boolean insertStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(Student student);

    /**
     * 查找某个考试缺考的学生
     */
    List<Student> selectStudentsOfMissingExam(Exam exam);

    AjaxResult updateHeadPic(Student student, MultipartFile file);
}
