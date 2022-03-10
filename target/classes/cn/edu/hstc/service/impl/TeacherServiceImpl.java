package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.TeacherDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.TeacherService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLDecoder;
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
    public boolean deleteTeacher(Teacher teacher) {
        return teacherDao.deleteTeacher(teacher) > 0;
    }

    @Override
    public AjaxResult updateHeadPic(Teacher teacher, MultipartFile file) {
        String des_path = null;  //头像文件文件夹地址
        try {
            des_path = URLDecoder.decode(ResourceUtils.getURL("classpath:").getPath(), "UTF-8") + "resources/head-pic/" + teacher.getTeaNum();
            File dir = new File(des_path);
            if (!dir.exists()) {
                //没有就创建
                dir.mkdirs();
            }
            String originalFilename = file.getOriginalFilename();   //前端文件名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));  //文件格式
            String fileName = ProjectUtil.getMD5String(originalFilename) + ProjectUtil.getUuid().substring(0, 8) + suffix;   //最终保存的文件名
            File headFile = new File(dir, fileName);  //创建文件对象，表示要保存的头像文件,第一个参数表示存储的文件夹，第二个参数表示存储的文件名
            file.transferTo(headFile);  //执行保存
            String head_pic = "/head-pic/" + teacher.getTeaNum() + "/" + fileName;  //新头像
            Teacher newTeacher = new Teacher();
            newTeacher.setId(teacher.getId());
            newTeacher.setCode(teacher.getCode());
            newTeacher.setHead_pic(head_pic);
            String old_head_pic = teacher.getHead_pic();  //旧头像
            if (updateTeacher(newTeacher)) {
                //更新用户session，更新页面头像显示
                teacher.setHead_pic(head_pic);
                ServletUtils.getSession().setAttribute("user", teacher);
                //删除服务器中的旧头像
                if (old_head_pic.contains(teacher.getTeaNum())) {
                    File oldHeadFile = new File(dir,old_head_pic.substring(old_head_pic.lastIndexOf("/")));
                    oldHeadFile.delete();
                }
            } else {
                return AjaxResult.error("头像更新程序异常！请稍后重试！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("程序异常");
        }
        return AjaxResult.success("头像修改成功！");
    }
}
