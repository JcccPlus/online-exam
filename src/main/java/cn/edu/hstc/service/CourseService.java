package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Course;

import java.util.List;

public interface CourseService {

    List<Course> selectCourseList(Course course);

    Course selectCourseById(Integer id);

    boolean insertCourse(Course course);

    boolean updateCourse(Course course);

    boolean deleteCourse(Course course);
}
