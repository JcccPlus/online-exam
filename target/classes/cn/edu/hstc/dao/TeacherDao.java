package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherDao {

    Teacher selectTeacherById(Integer id);

    Teacher findTeacher(@Param("teaNum") String teaNum, @Param("password") String password);

    List<Teacher> selectTeacherList(Teacher teacher);

    int insertTeacher(Teacher teacher);

    int updateTeacher(Teacher teacher);

    int deleteTeacher(Teacher teacher);

}
