package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Paper;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.service.PaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;
    @Autowired
    private PaperService paperService;

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

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody Exam exam){
        if(ObjectUtils.isEmpty(exam.getCode()) || ObjectUtils.isEmpty(exam.getPaperId())){
            return error("数据异常");
        }
        Paper param = new Paper();
        param.setId(exam.getPaperId());
        param.setCode(exam.getCode());
        List<Paper> papers = paperService.selectPaperList(param);
        if(papers.isEmpty()){
            return error("数据异常");
        }
        exam.setScore(papers.get(0).getScore());
        if(examService.insertExam(exam)){
            return success("考试创建成功！");
        }
        return error();
    }
}
