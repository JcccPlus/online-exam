package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Topic;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/topic")
public class TopicController {

    @RequestMapping("/list.html")
    public String list(Topic topic, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {

        return "teacher/tmain2";
    }
}
