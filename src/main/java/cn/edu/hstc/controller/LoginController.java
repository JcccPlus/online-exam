package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Admin;
import cn.edu.hstc.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/user/login")
    public String login(@RequestParam("user_id") String id, @RequestParam("user_password") String password, @RequestParam("user_role") String role, HttpSession session, Model model) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(password) || ObjectUtils.isEmpty(role)) {
            return "index";
        }
        if ("教师".equals(role)) {

        } else if ("学生".equals(role)) {

        } else if ("管理员".equals(role)) {
            Admin admin = adminService.getAdmin(id, password);
            if (admin != null) {
                session.setAttribute("user", admin);
                model.addAttribute("user", admin);
                return "admin/amain1";
            }
        } else {
            return "index";
        }
        model.addAttribute("message", "账号或密码错误");
        return "index";
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest req, HttpServletResponse resp){
        req.getSession().removeAttribute("user");
        return "index";
    }
}
