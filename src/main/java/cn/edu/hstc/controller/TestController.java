package cn.edu.hstc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/*
 *  @项目名：  online-exam
 *  @包名：    cn.edu.hstc.controller
 *  @文件名:   TestController
 *  @创建者:   JCHEN
 *  @创建时间:  2021/12/24 23:55
 *  @描述：    TODO
 */
@Controller
public class TestController {
    @RequestMapping({"index","getIndex"})
    public ModelAndView index(){
        //model.addAttribute("message","在线考试系统项目启动！");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        mv.addObject("message","在线考试系统项目启动！");
        return mv;
    }
}
