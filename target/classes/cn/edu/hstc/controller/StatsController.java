package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.service.RecordService;
import cn.edu.hstc.service.StatsService;
import cn.edu.hstc.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 统计分析相关控制器
 */
@Controller
@RequestMapping("/exam/stats")
public class StatsController extends BaseController {
    @Autowired
    private ExamService examService;
    @Autowired
    private RecordService recordService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private StatsService statsService;

    /*@GetMapping("/stats.html")
    public String toStatsHtml(@RequestParam("code") String examCode, Model model) {
        if (ObjectUtils.isEmpty(examCode)) {
            return "error/404";
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            return "error/404";
        }
        Exam param = new Exam();
        Paper paper = new Paper();
        paper.setTeaId(((Teacher) user).getId());
        param.setCode(examCode);
        param.setPaper(paper);
        List<Exam> exams = examService.selectExamList(param);
        if (exams.isEmpty()) {
            return "error/404";
        }
        Exam currentExam = exams.get(0);
        Stats currentStats = new Stats();
        List<Record> records = new ArrayList<>();
        Student student = new Student();
        student.setClassId(currentExam.getClassId());
        PageHelper.startPage(1, 1);
        long total = new PageInfo<Student>(studentService.selectStudentList(student)).getTotal();   //班级人数
        Date now = new Date();
        if (now.getTime() < currentExam.getStart().getTime()) {
            //考试未开始
            model.addAttribute("warn", "考试未开始");
        } else if (now.getTime() < currentExam.getEnd().getTime() && now.getTime() > currentExam.getStart().getTime()) {
            //考试进行中
            Record record = new Record();
            record.setExamId(currentExam.getId());
            record.setOrderBy("point desc");     //按分数倒序排序
            records = recordService.selectRecordList(record);
            if (records.isEmpty()) {
                model.addAttribute("warn", "该考试暂无任何数据");
            } else {
                //统计平均分 中位数 最低分 最高分 优秀人数 良好人数 不好不差人数 不及格人数 已交卷人数 正在考试人数（不涉及持久层，考试过程实时更新）
                int turnoutNum = records.size();  //已交卷人数
                int absenceNum = (int) (total - turnoutNum);   //正在考试人数
                double topScore = records.get(0).getPoint();   //最高分
                double lowScore = records.get(turnoutNum - 1).getPoint();   //最低分
                double medianScore = 0.0;   //中位数
                if (turnoutNum % 2 == 0) {
                    medianScore = (records.get((turnoutNum / 2) - 1).getPoint() + records.get(turnoutNum / 2).getPoint()) / 2;
                } else {
                    medianScore = records.get((turnoutNum / 2)).getPoint();
                }
                double examScore = currentExam.getScore();   //考试总分
                double passScore = examScore * 0.6;  //及格线
                double goodScore = examScore * 0.9;  //优秀线
                double medScore = examScore * 0.8;  //良好线
                int notPassNum = 0;  //不及格人数
                int goodNum = 0;   //优秀人数
                int medNum = 0;   //良好人数
                int norNum = 0;   //不好不差人数
                double classTotalScore = 0;   //班级总分
                for (Record stuRecord : records) {
                    double point = stuRecord.getPoint();
                    classTotalScore += point;
                    if (point >= goodScore) {
                        goodNum += 1;
                    } else if (point >= medScore) {
                        medNum += 1;
                    } else if (point >= passScore) {
                        norNum += 1;
                    } else {
                        notPassNum += 1;
                    }
                }
                double averageScore = classTotalScore / turnoutNum;  // 平均分=班级总分/已参加考试的人数
                currentStats.setTurnout(turnoutNum);
                currentStats.setAbsence(absenceNum);
                currentStats.setSTop(topScore);
                currentStats.setSLow(lowScore);
                currentStats.setSMedian(medianScore);
                currentStats.setGood(goodNum);
                currentStats.setMed(medNum);
                currentStats.setNor(norNum);
                currentStats.setBad(notPassNum);
                currentStats.setSAverage(averageScore);
            }
        } else if (now.getTime() > (currentExam.getEnd().getTime()+(60000))) { //+60000是以防系统自动提交时的时间已超过考试结束时间
            //考试已结束，新增缺考学生考试记录
            String result = recordService.hasStudentMissingExam(currentExam);
            if (result.equals("true")) {
                //存在缺考学生，重新记录统计结果
                Record record = new Record();
                record.setExamId(currentExam.getId());
                record.setOrderBy("point desc");
                records = recordService.selectRecordList(record);
                //先查看该考试是否已有统计记录
                Stats statsParam = new Stats();
                statsParam.setExamId(currentExam.getId());
                List<Stats> statsList = statsService.selectStatsList(statsParam);
                if (statsList.isEmpty()) {
                    //没有就插入
                    Stats newStats = statsService.getTheStats(currentExam, records);
                    statsService.insertStats(newStats);
                    currentStats = newStats;
                } else {
                    //有就更新
                    Stats stats = statsList.get(0);
                    //更新统计数据（需要重新统计所有字段）
                    Stats newStats = statsService.getTheStats(currentExam, records);
                    newStats.setId(stats.getId());
                    statsService.updateStats(newStats);
                    currentStats = newStats;
                }
            } else if (result.equals("false")) {
                //不存在缺考学生
                Record record = new Record();
                record.setExamId(currentExam.getId());
                records = recordService.selectRecordList(record);
                if (records.isEmpty()) {
                    model.addAttribute("warn", "该考试班级无学生数据");
                } else {
                    //先查看该考试是否已有统计记录
                    Stats statsParam = new Stats();
                    statsParam.setExamId(currentExam.getId());
                    List<Stats> statsList = statsService.selectStatsList(statsParam);
                    if (statsList.isEmpty()) {
                        //没有就插入
                        Stats newStats = statsService.getTheStats(currentExam, records);
                        statsService.insertStats(newStats);
                        currentStats = newStats;
                    } else {
                        //有就更新
                        Stats stats = statsList.get(0);
                        if (stats.getFlag().equals("否")) {
                            //不需要重新统计
                            currentStats = stats;
                        } else {
                            //该考试评卷未完成，需要重新统计所有字段
                            Stats newStats = statsService.getTheStats(currentExam, records);
                            newStats.setId(stats.getId());
                            statsService.updateStats(stats);
                            currentStats = newStats;
                        }
                    }
                }
            } else {
                model.addAttribute("msg", "程序异常");
                return "error/404";
            }
        }
        model.addAttribute("currentExam", currentExam);
        model.addAttribute("currentStats",currentStats);
        model.addAttribute("studentTotal", total);
        model.addAttribute("recordList",records);
        return "teacher/stats";
    }*/


    /**
     * 考试统计页面
     */
    @GetMapping("/stats.html")
    public String toStatsHtml(@RequestParam("code") String examCode, Model model) {
        if (ObjectUtils.isEmpty(examCode)) {
            return "error/404";
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Teacher)) {
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
        Exam param = new Exam();
        Paper paper = new Paper();
        paper.setTeaId(((Teacher) user).getId());   //查看是不是该老师的考试
        param.setCode(examCode);
        param.setPaper(paper);
        List<Exam> exams = examService.selectExamList(param);
        if (exams.isEmpty()) {
            return "error/404";
        }
        Exam currentExam = exams.get(0);
        Stats currentStats = new Stats();
        List<Record> records = new ArrayList<>();
        Student student = new Student();
        student.setClassId(currentExam.getClassId());
        PageHelper.startPage(1, 1);
        long total = new PageInfo<Student>(studentService.selectStudentList(student)).getTotal();   //班级人数
        Date now = new Date();
        if (now.getTime() < currentExam.getStart().getTime()) {
            //考试未开始
            model.addAttribute("warn", "考试未开始");
        } else if (now.getTime() < currentExam.getEnd().getTime()+30000 && now.getTime() > currentExam.getStart().getTime()) {
            //考试进行中
            Record record = new Record();
            record.setExamId(currentExam.getId());
            record.setOrderBy("point desc");     //按分数倒序排序
            records = recordService.selectRecordList(record);
            if (records.isEmpty()) {
                model.addAttribute("warn", "该考试暂无任何数据");
            } else {
                //平均分 中位数 最低分 最高分 优秀人数 良好人数 不好不差人数 不及格人数 已交卷人数 正在考试人数（前端刷新即统计，不涉及持久层）
                currentStats = statsService.getTheExamProcessStats(currentExam, records, total);
            }
        } else if (now.getTime() > (currentExam.getEnd().getTime()+30000)) { //+30000是以防系统自动提交时的时间已超过考试结束时间
            //考试已结束，新增缺考学生考试记录
            String result = recordService.hasStudentMissingExam(currentExam);
            if (!"error".equals(result)) {
                Record record = new Record();
                record.setExamId(currentExam.getId());
                record.setOrderBy("point desc");
                records = recordService.selectRecordList(record);
                if (records.isEmpty()) {
                    model.addAttribute("warn", "该考试班级无学生数据");
                }else{
                    //平均分 中位数 最低分 最高分 优秀人数 良好人数 不好不差人数 不及格人数 参加人数 缺考人数（前端刷新即统计，不涉及持久层）
                    currentStats = statsService.getTheStats(currentExam, records);
                }
            } else {
                model.addAttribute("msg", "程序异常");
                return "error/404";
            }
        }
        model.addAttribute("currentExam", currentExam);
        model.addAttribute("currentStats",currentStats);
        model.addAttribute("studentTotal", total);
        Collections.sort(records);   //按照学号排序
        model.addAttribute("recordList",records);
        return "teacher/stats";
    }

}
