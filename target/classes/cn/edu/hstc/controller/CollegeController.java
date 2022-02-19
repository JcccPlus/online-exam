package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.College;
import cn.edu.hstc.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    CollegeService collegeService;

    @RequestMapping("/college.html")
    public String list(College college, Model model) {
        List<College> colleges = collegeService.selectCollegeList(college);
        model.addAttribute("colleges", colleges);
        return "admin/amain1";
    }

    @RequestMapping("add")
    @ResponseBody
    public String add(@RequestParam("add_name") String name) {
        if(StringUtils.isEmpty(name)){
            return "学院名称不为空";
        }
        College college = new College();
        college.setName(name);
        if (collegeService.insertCollege(college)) {
            return "添加学院成功";
        } else {
            return "添加失败";
        }
    }
}
