package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Exam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/exam")
public class ExamController {

    @RequestMapping("/list.html")
    public String list(Exam exam, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {

        return "teacher/tmain4";
    }

    @RequestMapping("/current.html")
    public String getCurrentExam(Exam exam, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {

        return "student/smain1";
    }

    @RequestMapping("/previous.html")
    public String getPreviousExam(Exam exam, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {

        return "student/smain2";
    }
}
