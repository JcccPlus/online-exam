package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.*;
import cn.edu.hstc.vo.ExamAnswerVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/exam")
public class ExamController extends BaseController {
    @Autowired
    private ExamService examService;
    @Autowired
    private PaperService paperService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private TopicOfPaperService topicOfPaperService;
    @Autowired
    private AnswerOfStudentService answerOfStudentService;

    @RequestMapping("/list.html")
    public String list(Exam exam, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id desc");
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        Teacher teacher = (Teacher) user;
        Paper paper = new Paper();
        paper.setTeaId(teacher.getId());  //获得访问老师所布置的所有考试
        exam.setPaper(paper);
        if (!ObjectUtils.isEmpty(searchValue)) {
            exam.setSearchValue(searchValue);
        }
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        //PageHelper.orderBy("id desc");
        List<Exam> exams = examService.selectExamList(exam);
        model.addAttribute("examList", exams);
        model.addAttribute("pageInfo", new PageInfo<Exam>(exams));
        model.addAttribute("searchValue", searchValue);
        return "teacher/tmain4";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        return "redirect:/exam/list.html";
    }

    @RequestMapping("/current.html")
    public String getCurrentExam(Exam exam, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Student)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        Student currentStudent = (Student) user;
        if (!ObjectUtils.isEmpty(searchValue)) {
            exam.setSearchValue(searchValue);
        }
        exam.setClassId(currentStudent.getClassId());
        PageHelper.orderBy("start");
        List<Exam> exams = examService.selectCurrentExam(exam, currentStudent.getId());
        model.addAttribute("examList", exams);
        model.addAttribute("searchValue", searchValue);
        return "student/smain1";
    }

    @RequestMapping("/searchCurrentExam")
    public String searchCurrentExam(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        return "redirect:/exam/current.html";
    }

    @RequestMapping("/previous.html")
    public String getPreviousExam(Record record, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Student)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        Student currentStudent = (Student) user;
        if (!ObjectUtils.isEmpty(searchValue)) {
            record.setSearchValue(searchValue);
        }
        if (ObjectUtils.isEmpty(pageNum)) {
            pageNum = "1";
        }
        PageHelper.startPage(Integer.parseInt(pageNum), 10, "id desc");
        //record.setOrderBy("finish_time desc");
        record.setStuId(currentStudent.getId());
        List<Record> records = recordService.selectStudentRecordList(record);
        model.addAttribute("recordList", records);
        model.addAttribute("searchValue", searchValue);
        model.addAttribute("pageInfo", new PageInfo<Record>(records));
        return "student/smain2";
    }

    @RequestMapping("/searchPreviousExam")
    public String searchPreviousExam(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        return "redirect:/exam/previous.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody Exam exam) {
        if (ObjectUtils.isEmpty(exam) || ObjectUtils.isEmpty(exam.getCode()) || ObjectUtils.isEmpty(exam.getPaperId())) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            return error("无访问权限");
        }
        Teacher teacher = (Teacher) user;
        Paper param = new Paper();
        param.setId(exam.getPaperId());
        param.setCode(exam.getCode());
        param.setTeaId(teacher.getId());
        List<Paper> papers = paperService.selectPaperList(param);
        if (papers.isEmpty()) {
            return error("数据异常");
        }
        exam.setScore(papers.get(0).getScore());
        if (exam.getLateTime() == null) {
            exam.setLateTime(0);
        }
        if (examService.insertExam(exam)) {
            return success("考试创建成功！");
        }
        return error();
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("examId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            return error("无访问权限");
        }
        Exam exam = new Exam();
        exam.setId(id);
        exam.setCode(code);
        List<Exam> exams = examService.selectExamList(exam);
        Exam exam1 = exams.get(0);
        Date startTime = exam1.getStart();
        Date end = exam1.getEnd();
        Date now = new Date();
        if (now.getTime() > end.getTime()) {
            return error("考试已结束，不可取消！");
        }
        if (now.getTime() > startTime.getTime()) {
            return error("考试正在进行中，不可取消！");
        }
        if (examService.deleteExam(exam)) {
            return success("取消成功");
        } else {
            return error();
        }
    }

    @GetMapping("/list.html/{code}")
    public String toExamInfoHtml(@PathVariable(value = "code", required = true) String code, Model model, @ModelAttribute("exportMsg") String exportMsg) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        Exam param = new Exam();
        param.setCode(code);
        Teacher teacher = (Teacher) user;
        Paper paper = new Paper();
        paper.setTeaId(teacher.getId());   //判断是不是访问老师所布置的考试
        param.setPaper(paper);
        List<Exam> exams = examService.selectExamList(param);
        if (exams.isEmpty()) {
            model.addAttribute("msg", "数据异常");
            return "error/404";
        }
        Exam exam = exams.get(0);
        if ("error".equals(recordService.hasStudentMissingExam(exam))) {
            model.addAttribute("msg", "程序异常");
            return "error/404";
        }
        model.addAttribute("currentExam", exam);
        Record record = new Record();
        record.setExamId(exam.getId());
        /*record.setOrderBy("point desc");*/  //mybatis排序
        PageHelper.orderBy("point desc");   //pageHelper排序
        List<Record> records = recordService.selectRecordList(record);
        model.addAttribute("recordList", records);
        Date now = new Date();
        if (exam.getStart().getTime() > now.getTime()) {
            model.addAttribute("msg", "未开始");
        } else if ((exam.getStart().getTime() < now.getTime()) && (exam.getEnd().getTime() > now.getTime())) {
            model.addAttribute("msg", "进行中");
        } else if (exam.getEnd().getTime() < now.getTime()) {
            model.addAttribute("msg", "已结束");
        }
        if (!ObjectUtils.isEmpty(exportMsg)) {
            model.addAttribute("exportMsg", exportMsg);
        }
        return "teacher/examInfo";
    }

    @GetMapping("/current/exam.html/{code}")
    public String enterExam(@PathVariable(value = "code", required = true) String examCode, Model model) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Student)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        Student currentStudent = (Student) user;
        Exam param = new Exam();
        param.setCode(examCode);  //考试唯一码
        param.setClassId(currentStudent.getClassId());  //考试班级   //考试学生
        List<Exam> exams = examService.selectCurrentExam(param, currentStudent.getId());
        if (exams.isEmpty()) {
            //判断访问者是否非法
            model.addAttribute("msg", "非法访问");
            return "error/404";
        }
        Exam currentExam = exams.get(0);
        Date now = new Date();
        Date start = currentExam.getStart();
        Date end = currentExam.getEnd();
        //判断考试是否开始
        if (now.getTime() < start.getTime()) {
            model.addAttribute("msg", "非法访问");
            return "error/404";
        }
        //判断考试是否过期
        if (now.getTime() > end.getTime()) {
            model.addAttribute("msg", "非法访问");
            return "error/404";
        }
        //判断考试是否迟到（待完善，逻辑存在bug）
        model.addAttribute("currentExam", currentExam);
        //获取该试卷所有题目
        TopicOfPaper topicOfPaper = new TopicOfPaper();
        topicOfPaper.setPaperId(currentExam.getPaper().getId());
        List<TopicOfPaper> topics = topicOfPaperService.selectTopicOfPaperList(topicOfPaper);
        //将题目按题型分配
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
        logger.info("考试前session过期时间为"+(getSession().getMaxInactiveInterval()/3600)+"小时！");
        getSession().setMaxInactiveInterval(getSession().getMaxInactiveInterval()+(currentExam.getTime()+10)*60);
        logger.info(currentStudent.getName()+"进入考试！已将session过期时间调整至"+(getSession().getMaxInactiveInterval()/3600)+"小时！");
        model.addAttribute("singleChoiceTopics", singleChoiceTopics);
        model.addAttribute("moreChoiceTopics", moreChoiceTopics);
        model.addAttribute("estimateTopics", estimateTopics);
        model.addAttribute("fillEmptyTopics", fillEmptyTopics);
        model.addAttribute("subjectiveTopics", subjectiveTopics);
        return "student/onlineExam";
    }

    @PostMapping("/submitExam")
    @ResponseBody
    public AjaxResult submitExam(@RequestBody(required = true) List<ExamAnswerVo> vos, @RequestParam(value = "code", required = true) String code) {
        return examService.submitExam(code, vos);
    }

    @GetMapping("/record.html")
    public String toStudentRecordHtml(String code, Model model) {
        if (ObjectUtils.isEmpty(code)) {
            return "error/404";
        }
        Object user = getSession().getAttribute("user");
        Record param = new Record();
        param.setCode(code);
        if (user instanceof Teacher) {  //如果是老师访问
            List<Record> records = recordService.selectRecordList(param);
            if (records.isEmpty()) {
                return "error/404";
            }
            Record record = records.get(0);
            if (!record.getExam().getPaper().getCourse().getTeacher().getId().equals(((Teacher) user).getId())) {
                //不是该老师布置的考试
                return "error/404";
            }
            if (!record.getState().equals("完成")) {
                //不是已经完成的考试
                return "error/404";
            }
            model.addAttribute("currentRecord", record);
            AnswerOfStudent an = new AnswerOfStudent();
            an.setRecordId(record.getId());
            an.setOrderBy("pt_id");
            List<AnswerOfStudent> answerList = answerOfStudentService.selectAnswerOfStudentList(an);
            if (answerList.isEmpty()) {
                return "error/404";
            }
            //将题目按题型分配
            List<TopicOfPaper> singleChoiceTopics = new ArrayList<>();  //记录单选题
            List<TopicOfPaper> moreChoiceTopics = new ArrayList<>();  //记录多选题
            List<TopicOfPaper> estimateTopics = new ArrayList<>();   //记录判断题
            List<TopicOfPaper> fillEmptyTopics = new ArrayList<>();   //记录填空题
            List<TopicOfPaper> subjectiveTopics = new ArrayList<>();  //记录主观题
            for (AnswerOfStudent answerOfStudent : answerList) {
                switch (answerOfStudent.getTopic().getTopic().getType().getName()) {
                    case "单选题":
                        singleChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "多选题":
                        moreChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "判断题":
                        estimateTopics.add(answerOfStudent.getTopic());
                        break;
                    case "填空题":
                        fillEmptyTopics.add(answerOfStudent.getTopic());
                        break;
                    case "主观题":
                        subjectiveTopics.add(answerOfStudent.getTopic());
                        break;
                }
            }
            model.addAttribute("singleChoiceTopics", singleChoiceTopics);
            model.addAttribute("moreChoiceTopics", moreChoiceTopics);
            model.addAttribute("estimateTopics", estimateTopics);
            model.addAttribute("fillEmptyTopics", fillEmptyTopics);
            model.addAttribute("subjectiveTopics", subjectiveTopics);
            model.addAttribute("answerList", answerList);
            return "teacher/stu-record";
        } else if (user instanceof Student) {  //如果是学生访问
            param.setStuId(((Student) user).getId());
            List<Record> records = recordService.selectRecordList(param);
            if (records.isEmpty()) {
                return "error/404";
            }
            model.addAttribute("currentRecord", records.get(0));
            AnswerOfStudent an = new AnswerOfStudent();
            an.setRecordId(records.get(0).getId());
            an.setOrderBy("pt_id");
            List<AnswerOfStudent> answerList = answerOfStudentService.selectAnswerOfStudentList(an);
            if (answerList.isEmpty()) {
                if (!records.get(0).getState().equals("缺考")) {
                    return "error/404";
                } else {
                    //缺考考生查看的考试详情
                    //获取该试卷所有题目
                    TopicOfPaper topicOfPaper = new TopicOfPaper();
                    topicOfPaper.setPaperId(records.get(0).getExam().getPaper().getId());
                    List<TopicOfPaper> topics = topicOfPaperService.selectTopicOfPaperList(topicOfPaper);
                    //将题目按题型分配
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
                    model.addAttribute("singleChoiceTopics", singleChoiceTopics);
                    model.addAttribute("moreChoiceTopics", moreChoiceTopics);
                    model.addAttribute("estimateTopics", estimateTopics);
                    model.addAttribute("fillEmptyTopics", fillEmptyTopics);
                    model.addAttribute("subjectiveTopics", subjectiveTopics);
                    model.addAttribute("answerList", answerList);
                    return "student/record";
                }
            }
            //将题目按题型分配
            List<TopicOfPaper> singleChoiceTopics = new ArrayList<>();  //记录单选题
            List<TopicOfPaper> moreChoiceTopics = new ArrayList<>();  //记录多选题
            List<TopicOfPaper> estimateTopics = new ArrayList<>();   //记录判断题
            List<TopicOfPaper> fillEmptyTopics = new ArrayList<>();   //记录填空题
            List<TopicOfPaper> subjectiveTopics = new ArrayList<>();  //记录主观题
            for (AnswerOfStudent answerOfStudent : answerList) {
                switch (answerOfStudent.getTopic().getTopic().getType().getName()) {
                    case "单选题":
                        singleChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "多选题":
                        moreChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "判断题":
                        estimateTopics.add(answerOfStudent.getTopic());
                        break;
                    case "填空题":
                        fillEmptyTopics.add(answerOfStudent.getTopic());
                        break;
                    case "主观题":
                        subjectiveTopics.add(answerOfStudent.getTopic());
                        break;
                }
            }
            model.addAttribute("singleChoiceTopics", singleChoiceTopics);
            model.addAttribute("moreChoiceTopics", moreChoiceTopics);
            model.addAttribute("estimateTopics", estimateTopics);
            model.addAttribute("fillEmptyTopics", fillEmptyTopics);
            model.addAttribute("subjectiveTopics", subjectiveTopics);
            model.addAttribute("answerList", answerList);
            return "student/record";
        } else {
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
    }

    /**
     * 评卷页面
     */
    @GetMapping("/check.html")
    public String toCheckStudentRecord(String code, Model model) {
        if (ObjectUtils.isEmpty(code)) {
            return "error/404";
        }
        Object user = getSession().getAttribute("user");
        Record param = new Record();
        param.setCode(code);
        if (user instanceof Teacher) {   //必须是老师访问
            List<Record> records = recordService.selectRecordList(param);
            if (records.isEmpty()) {
                return "error/404";
            }
            Record record = records.get(0);
            if (!record.getExam().getPaper().getCourse().getTeacher().getId().equals(((Teacher) user).getId())) {
                //不是该老师布置的考试
                return "error/404";
            }
            if (!record.getState().equals("待评卷")) {
                //该记录不为’待评卷‘状态
                return "error/404";
            }
            model.addAttribute("currentRecord", record);
            AnswerOfStudent an = new AnswerOfStudent();
            an.setRecordId(record.getId());
            an.setOrderBy("pt_id");
            List<AnswerOfStudent> answerList = answerOfStudentService.selectAnswerOfStudentList(an);
            if (answerList.isEmpty()) {
                return "error/404";
            }
            //将题目按题型分配
            List<TopicOfPaper> singleChoiceTopics = new ArrayList<>();  //记录单选题
            List<TopicOfPaper> moreChoiceTopics = new ArrayList<>();  //记录多选题
            List<TopicOfPaper> estimateTopics = new ArrayList<>();   //记录判断题
            List<TopicOfPaper> fillEmptyTopics = new ArrayList<>();   //记录填空题
            List<TopicOfPaper> subjectiveTopics = new ArrayList<>();  //记录主观题
            for (AnswerOfStudent answerOfStudent : answerList) {
                switch (answerOfStudent.getTopic().getTopic().getType().getName()) {
                    case "单选题":
                        singleChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "多选题":
                        moreChoiceTopics.add(answerOfStudent.getTopic());
                        break;
                    case "判断题":
                        estimateTopics.add(answerOfStudent.getTopic());
                        break;
                    case "填空题":
                        fillEmptyTopics.add(answerOfStudent.getTopic());
                        break;
                    case "主观题":
                        subjectiveTopics.add(answerOfStudent.getTopic());
                        break;
                }
            }
            model.addAttribute("singleChoiceTopics", singleChoiceTopics);
            model.addAttribute("moreChoiceTopics", moreChoiceTopics);
            model.addAttribute("estimateTopics", estimateTopics);
            model.addAttribute("fillEmptyTopics", fillEmptyTopics);
            model.addAttribute("subjectiveTopics", subjectiveTopics);
            model.addAttribute("answerList", answerList);
        } else {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        return "teacher/check-record";
    }

    /**
     * 老师提交评卷接口
     */
    @PostMapping("/markExam")
    @ResponseBody
    public AjaxResult markExam(@RequestBody Double[] score, @RequestParam("code") String recordCode) {
        if (ObjectUtils.isEmpty(score) || ObjectUtils.isEmpty(recordCode)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            //权限控制
            return error("无访问权限");
        }
        Record param = new Record();
        param.setCode(recordCode);
        List<Record> records = recordService.selectRecordList(param);
        if (records.isEmpty()) {
            return error("数据异常");
        }
        Record record = records.get(0);
        if (!record.getExam().getPaper().getCourse().getTeacher().getId().equals(((Teacher) user).getId())) {
            //评分人是否合法
            return error("无访问权限");
        }
        if (!"待评卷".equals(record.getState())) {
            return error("请勿重复评分！");
        }
        return examService.markExam(record, score);
    }
}
