package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Paper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/paper")
public class PaperController {

    @RequestMapping("/list.html")
    public String list(Paper paper, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum){

        return "teacher/tmain3";
    }
}
