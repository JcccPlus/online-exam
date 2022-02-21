package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Admin;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.AdminService;
import cn.edu.hstc.service.StudentService;
import cn.edu.hstc.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    @RequestMapping("/user/login")
    public String login(@RequestParam("user_id") String id, @RequestParam("user_password") String password, @RequestParam("user_role") String role, HttpSession session, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(password) || ObjectUtils.isEmpty(role)) {
            return "redirect:/";
        }
        String message = "";
        if ("教师".equals(role)) {
            Teacher param = new Teacher();
            param.setTeaNum(id);
            List<Teacher> teachers = teacherService.selectTeacherList(param);
            if (teachers.isEmpty()) {
                message = "账号不存在";
            } else {
                if (teachers.size() == 1) {
                    Teacher teacher = teachers.get(0);
                    if (teacher.getPassword().equals(password)) {
                        session.setAttribute("user", teacher);
                        return "redirect:/teacher/main.html";
                    } else {
                        message = "账号或密码错误";
                    }
                } else {
                    message = "系统数据异常，请稍后重试";
                }
            }
        } else if ("学生".equals(role)) {
            Student param = new Student();
            param.setStuNum(id);
            List<Student> students = studentService.selectStudentList(param);
            if (students.isEmpty()) {
                message = "账号不存在";
            } else {
                if (students.size() == 1) {
                    Student student = students.get(0);
                    if (student.getPassword().equals(password)) {
                        session.setAttribute("user", student);
                        return "redirect:/student/main.html";
                    } else {
                        message = "账号或密码错误";
                    }
                } else {
                    message = "系统数据异常，请稍后重试";
                }
            }
        } else if ("管理员".equals(role)) {
            Admin admin = adminService.getAdmin(id, password);
            if (admin != null) {
                session.setAttribute("user", admin);
                return "redirect:/college/list.html";
            } else {
                message = "账号或密码错误";
            }
        } else {
            message = "请确认系统角色是否勾选";
        }
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/";
    }

    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login.html";
    }
}
