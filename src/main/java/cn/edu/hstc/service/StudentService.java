package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Student;

import java.util.List;

public interface StudentService {

    Student selectStudentById(Integer id);

    Student findStudent(String stuNum, String password);

    List<Student> selectStudentList(Student student);

    boolean insertStudent(Student student);

    boolean updateStudent(Student student);

    boolean deleteStudent(Integer id);

    boolean deleteStudentByCode(String code);

}
