package cn.edu.hstc.service;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Course;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CourseService {

    List<Course> selectCourseList(Course course);

    Course selectCourseById(Integer id);

    boolean insertCourse(Course course);

    boolean updateCourse(Course course);

    boolean deleteCourse(Course course);

    AjaxResult insertMoreCourse(List<Course> courses, HttpSession session);
}
