package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseDao {

    List<Course> selectCourseList(Course course);

    Course selectCourseById(Integer id);

    int insertCourse(Course course);

    int updateCourse(Course course);

    int deleteCourse(Course course);

}
