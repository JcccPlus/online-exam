package cn.edu.hstc.dao;

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

    int deleteStudent(Integer id);

    int deleteStudentByCode(String code);

}
