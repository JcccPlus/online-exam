package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.TeacherDao;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.TeacherService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherDao teacherDao;

    @Override
    public Teacher selectTeacherById(Integer id) {
        return teacherDao.selectTeacherById(id);
    }

    @Override
    public Teacher findTeacher(String teaNum, String password) {
        return teacherDao.findTeacher(teaNum, password);
    }

    @Override
    public List<Teacher> selectTeacherList(Teacher teacher) {
        return teacherDao.selectTeacherList(teacher);
    }

    @Override
    public boolean insertTeacher(Teacher teacher) {
        //生成32位教师唯一识别码
        String code = ProjectUtil.getUuid().substring(0, 16) + ProjectUtil.getMD5String(teacher.getTeaNum()).substring(0, 16);
        teacher.setCode(code);
        //男女默认头像
        if ("男".equals(teacher.getGender())) {
            teacher.setHead_pic("/images/boy.png");
        } else if ("女".equals(teacher.getGender())) {
            teacher.setHead_pic("/images/girl.png");
        } else {
            return false;
        }
        teacher.setPassword(teacher.getTeaNum());
        return teacherDao.insertTeacher(teacher) > 0;
    }

    @Override
    public boolean updateTeacher(Teacher teacher) {
        return teacherDao.updateTeacher(teacher) > 0;
    }

    @Override
    public boolean deleteTeacherByCode(String code) {
        return teacherDao.deleteTeacherByCode(code) > 0;
    }

    @Override
    public boolean deleteTeacher(Integer id) {
        return teacherDao.deleteTeacher(id) > 0;
    }
}
