package cn.edu.hstc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping({"index","getIndex"})
    public ModelAndView index(){
        //model.addAttribute("message","在线考试系统项目启动！");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        mv.addObject("message","在线考试系统项目启动！");
        return mv;
    }
    @RequestMapping("/test")
    @ResponseBody
    public String test(){
        String sql = "insert into tb_type(name) values('主观题')";
        int row = jdbcTemplate.update(sql);
        if(row>0){
            return "添加成功";
        }
        return "添加失败";
    }
}
