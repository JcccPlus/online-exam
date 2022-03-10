package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentDao {

    Student selectStudentById(Integer id);

    Student findStudent(@Param("stuNum") String stuNum, @Param("password") String password);

    List<Student> selectStudentList(Student student);

    int insertStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(Student student);

    /**
     * 查找某个考试缺考的学生
     */
    List<Student> selectStudentsOfMissingExam(Exam exam);

}
