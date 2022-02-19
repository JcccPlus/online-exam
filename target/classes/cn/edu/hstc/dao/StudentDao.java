package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Student;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentDao {

    Student findStudent(Student student);

    List<Student> selectStudents(Student student);

    int insertStudent(Student student);

    int updateStudent(Student student);

    int deleteStudent(String code);

}
