package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.*;
import cn.edu.hstc.vo.AutomaticPaperVo;
import cn.edu.hstc.vo.ManualPaperVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/paper")
public class PaperController extends BaseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private ExamService examService;
    @Autowired
    private TopicOfPaperService topicOfPaperService;
    @Autowired
    private CollegeService collegeService;


    @RequestMapping("/list.html")
    public String list(Paper paper, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id desc");
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
        Teacher teacher = (Teacher) user;
        paper.setTeaId(teacher.getId());
        Course param = new Course();
        param.setTeaId(teacher.getId());
        List<Course> currentCourses = courseService.selectCourseList(param);
        model.addAttribute("courseList", currentCourses);
        List<Type> types = typeService.selectTypeList(new Type());
        List<Level> levels = levelService.selectLevelList(new Level());
        model.addAttribute("typeList", types);
        model.addAttribute("levelList", levels);
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        if (!ObjectUtils.isEmpty(searchValue)) {
            paper.setSearchValue(searchValue);
        }
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Paper> papers = paperService.selectPaperList(paper);
        model.addAttribute("paperList", papers);
        model.addAttribute("pageInfo", new PageInfo<Paper>(papers));
        model.addAttribute("searchValue", searchValue);
        return "teacher/tmain3";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        return "redirect:/paper/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(Paper paper) {
        return success();
    }

    @PostMapping("/autoGenerate")
    @ResponseBody
    public AjaxResult autoGeneratePaper(@RequestBody List<AutomaticPaperVo> vos) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        return paperService.autoGeneratePaper(vos);
    }

    @PostMapping("/manuallyGenerate")
    @ResponseBody
    public AjaxResult manuallyGeneratePaper(@RequestBody List<ManualPaperVo> vos) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        return paperService.manuallyGeneratePaper(vos, false);
    }

    @PostMapping("/manuallyGeneratePlus")
    @ResponseBody
    public AjaxResult manuallyGeneratePlus(@RequestBody List<ManualPaperVo> vos) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        return paperService.manuallyGeneratePaper(vos, true);
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Paper paper) {
        if (ObjectUtils.isEmpty(paper.getId()) || ObjectUtils.isEmpty(paper.getCode())) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        if (paperService.updatePaper(paper)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("paperId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        Exam exam = new Exam();
        exam.setPaperId(id);
        if (examService.selectExamList(exam).size() > 0) {
            return error("删除失败！该试卷已生成相关考试！");
        }
        Paper param = new Paper();
        param.setId(id);
        param.setCode(code);
        if (paperService.deletePaper(param)) {
            return success("删除成功");
        } else {
            return error();
        }
    }

    @GetMapping("/list.html/{code}")
    public String toPaperInfoHtml(@PathVariable(value = "code", required = true) String code, Model model) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
        Paper param = new Paper();
        param.setCode(code);
        param.setTeaId(((Teacher) user).getId());
        List<Paper> papers = paperService.selectPaperList(param);
        if (papers.isEmpty()) {
            model.addAttribute("msg", "数据异常");
            return "error/404";
        }
        TopicOfPaper topicOfPaper = new TopicOfPaper();
        topicOfPaper.setPaperId(papers.get(0).getId());
        List<TopicOfPaper> topics = topicOfPaperService.selectTopicOfPaperList(topicOfPaper);
        List<TopicOfPaper> singleChoiceTopics = new ArrayList<>();  //记录单选题
        List<TopicOfPaper> moreChoiceTopics = new ArrayList<>();  //记录多选题
        List<TopicOfPaper> estimateTopics = new ArrayList<>();   //记录判断题
        List<TopicOfPaper> fillEmptyTopics = new ArrayList<>();   //记录填空题
        List<TopicOfPaper> subjectiveTopics = new ArrayList<>();  //记录主观题
        for (TopicOfPaper topic : topics) {
            switch (topic.getTopic().getType().getName()) {
                case "单选题":
                    singleChoiceTopics.add(topic);
                    break;
                case "多选题":
                    moreChoiceTopics.add(topic);
                    break;
                case "判断题":
                    estimateTopics.add(topic);
                    break;
                case "填空题":
                    fillEmptyTopics.add(topic);
                    break;
                case "主观题":
                    subjectiveTopics.add(topic);
                    break;
            }
        }
        model.addAttribute("topicList", topics);
        model.addAttribute("singleChoiceTopics", singleChoiceTopics);
        model.addAttribute("moreChoiceTopics", moreChoiceTopics);
        model.addAttribute("estimateTopics", estimateTopics);
        model.addAttribute("fillEmptyTopics", fillEmptyTopics);
        model.addAttribute("subjectiveTopics", subjectiveTopics);
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        return "teacher/paperInfo";
    }

    /*@GetMapping("/downPaper/{code}")
    @ResponseBody
    public AjaxResult downPaper(@PathVariable(value = "code", required = true) String code){
        Paper param = new Paper();
        param.setCode(code);
        try{
            Teacher teacher = (Teacher) getSession().getAttribute("user");
            param.setTeaId(teacher.getId());
        }catch (Exception e){
            return error("无权限访问");
        }
        List<Paper> papers = paperService.selectPaperList(param);
        if (papers.isEmpty()) {
            return error("数据异常");
        }
        return paperService.downPaper(papers.get(0));
    }*/

    @GetMapping("/downPaper/{code}")
    public String downPaper(@PathVariable(value = "code", required = true) String code, Model model) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
        Paper param = new Paper();
        param.setCode(code);
        param.setTeaId(((Teacher) user).getId());
        List<Paper> papers = paperService.selectPaperList(param);
        if (papers.isEmpty()) {
            model.addAttribute("msg", "数据异常");
            return "error/404";
        }
        AjaxResult ajaxResult = paperService.downPaper(papers.get(0));
        Object msg = ajaxResult.get("msg");
        if (msg.equals("程序异常")) {
            model.addAttribute("msg", "程序异常");
            return "error/404";
        }
        return "redirect:/paper/paperInfo.html/" + code;
    }
}
