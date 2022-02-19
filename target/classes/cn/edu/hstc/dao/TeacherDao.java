package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Teacher;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TeacherDao {

    Teacher findTeacher(Teacher teacher);

    List<Teacher> selectTeachers(Teacher teacher);

    int insertTeacher(Teacher teacher);

    int updateTeacher(Teacher teacher);

    int deleteTeacher(String code);

}
