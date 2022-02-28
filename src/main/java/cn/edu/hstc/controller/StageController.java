package cn.edu.hstc.controller;

import cn.edu.hstc.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/stage")
public class StageController {
    @Autowired
    private StageService stageService;
}
