package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Course;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/course")
public class CourseController {

    @RequestMapping("/list.html")
    public String list(Course course, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum){

        return "teacher/tmain1";
    }
}
