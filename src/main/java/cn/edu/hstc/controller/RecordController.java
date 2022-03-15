package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Paper;
import cn.edu.hstc.pojo.Record;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.service.RecordService;
import cn.edu.hstc.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/record")
public class RecordController extends BaseController {
    @Autowired
    private RecordService recordService;
    @Autowired
    private ExamService examService;

    @RequestMapping(value = "/export")
    public String export(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes, @RequestParam("code") String examCode) {
        if (ObjectUtils.isEmpty(examCode)) {
            return "error/404";
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        } else {
            Exam param = new Exam();
            param.setCode(examCode);
            Paper paper = new Paper();
            paper.setTeaId(((Teacher) user).getId());   //是不是访问的考试
            param.setPaper(paper);
            List<Exam> exams = examService.selectExamList(param);
            if (exams.isEmpty()) {
                model.addAttribute("msg", "无访问权限");
                return "error/404";
            }
            Exam exam = exams.get(0);
            Record record = new Record();
            record.setExamId(exam.getId());
            record.setOrderBy("point desc");
            List<Record> records = recordService.selectRecordList(record);
            if (records.isEmpty()) {
                redirectAttributes.addFlashAttribute("exportMsg", "该考试无成绩记录可供下载");
                return "redirect:/exam/list.html/" + examCode;
            } else {
                String message = null;
                try {
                    message = ExcelUtil.OutExcel(request, response, records);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("msg", "程序异常");
                    return "error/404";
                }
                if (message.equals("fail")) {
                    model.addAttribute("msg", "程序异常");
                    return "error/404";
                } else {
                    return message;
                }
            }
        }
    }
}
