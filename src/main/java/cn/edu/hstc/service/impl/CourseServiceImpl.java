package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.CourseDao;
import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.service.CourseService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseDao courseDao;

    @Override
    public List<Course> selectCourseList(Course course) {
        return courseDao.selectCourseList(course);
    }

    @Override
    public Course selectCourseById(Integer id) {
        return courseDao.selectCourseById(id);
    }

    @Override
    public boolean insertCourse(Course course) {
        course.setCode(ProjectUtil.getUuid().substring(0, 16));
        return courseDao.insertCourse(course) > 0;
    }

    @Override
    public boolean updateCourse(Course course) {
        return courseDao.updateCourse(course) > 0;
    }

    @Override
    public boolean deleteCourse(Course course) {
        return courseDao.deleteCourse(course) > 0;
    }
}
