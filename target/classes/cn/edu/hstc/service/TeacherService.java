package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Teacher;

import java.util.List;

public interface TeacherService {

    Teacher selectTeacherById(Integer id);

    Teacher findTeacher(String teaNum, String password);

    List<Teacher> selectTeacherList(Teacher teacher);

    boolean insertTeacher(Teacher teacher);

    boolean updateTeacher(Teacher teacher);

    boolean deleteTeacher(Teacher teacher);

}
