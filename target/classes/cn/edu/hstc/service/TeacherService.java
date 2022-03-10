package cn.edu.hstc.service;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Teacher;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TeacherService {

    Teacher selectTeacherById(Integer id);

    Teacher findTeacher(String teaNum, String password);

    List<Teacher> selectTeacherList(Teacher teacher);

    boolean insertTeacher(Teacher teacher);

    boolean updateTeacher(Teacher teacher);

    boolean deleteTeacher(Teacher teacher);

    AjaxResult updateHeadPic(Teacher teacher, MultipartFile file);

}
