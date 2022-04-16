package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.CourseDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.CourseService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
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

    @Override
    @Transactional
    public AjaxResult insertMoreCourse(List<Course> courses, HttpSession session) {
        Teacher currentTeacher = (Teacher) session.getAttribute("user");
        int rows = 0;
        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (ObjectUtils.isEmpty(course.getName())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return AjaxResult.error("第" + (i + 1) + "门课程的名称为空，请重新输入！");
            }
            course.setTeaId(currentTeacher.getId());
            course.setCode(ProjectUtil.getUuid().substring(0, 16));
            rows += courseDao.insertCourse(course);
        }
        if (rows > 0) {
            return AjaxResult.success("成功添加" + rows + "门课程！");
        }
        return AjaxResult.error();
    }
}
