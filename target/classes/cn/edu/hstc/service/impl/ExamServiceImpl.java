package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.AnswerOfStudentDao;
import cn.edu.hstc.dao.ExamDao;
import cn.edu.hstc.dao.RecordDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.service.TopicOfPaperService;
import cn.edu.hstc.util.ProjectUtil;
import cn.edu.hstc.vo.ExamAnswerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamDao examDao;
    @Autowired
    private TopicOfPaperService topicOfPaperService;
    @Autowired
    private RecordDao recordDao;
    @Autowired
    private AnswerOfStudentDao answerOfStudentDao;

    @Override
    public List<Exam> selectExamList(Exam exam) {
        return examDao.selectExamList(exam);
    }

    @Override
    public Exam selectExamById(Integer id) {
        return examDao.selectExamById(id);
    }

    @Override
    public boolean insertExam(Exam exam) {
        Date start = exam.getStart();
        long startTime = start.getTime();
        long endTime = exam.getTime() * 60 * 1000;
        Date end = new Date(startTime + endTime);
        exam.setEnd(end);
        exam.setCode(ProjectUtil.getUuid());
        exam.setCreateTime(DateUtils.getNowDate());
        return examDao.insertExam(exam) > 0;
    }

    @Override
    public boolean updateExam(Exam exam) {
        exam.setUpdateTime(DateUtils.getNowDate());
        return examDao.updateExam(exam) > 0;
    }

    @Override
    public boolean deleteExam(Exam exam) {
        return examDao.deleteExam(exam) > 0;
    }

    @Override
    public List<Exam> selectCurrentExam(Exam exam, Integer stuId) {
        return examDao.selectCurrentExam(exam, stuId);
    }

    @Override
    @Transactional
    public AjaxResult submitExam(String code, List<ExamAnswerVo> vos) {
        if (ObjectUtils.isEmpty(code) || ObjectUtils.isEmpty(vos)) {
            return AjaxResult.error("数据异常");
        }
        Object user = ServletUtils.getSession().getAttribute("user");
        if (!(user instanceof Student)) {
            return AjaxResult.error("无访问权限");
        }
        Student currentStudent = (Student) user;
        Date finishTime = new Date(); //记录学生交卷时间
        Exam param = new Exam();
        param.setCode(code);  //考试唯一码
        param.setClassId(currentStudent.getClassId());  //考试班级   //考试学生
        List<Exam> list = selectExamList(param);
        if(list.isEmpty()){
            //判断访问者是否非法（判断该考试是否属于该学生班级）
            return AjaxResult.error("非法访问");
        }
        List<Exam> exams = selectCurrentExam(param, currentStudent.getId());
        if (exams.isEmpty()) {
            //判断是否重复提交
            return AjaxResult.error("请勿重复交卷！");
        }
        Exam currentExam = exams.get(0);  //得到当前考试数据
        Date start = currentExam.getStart();
        Date end = currentExam.getEnd();
        //判断考试是否开始
        if (finishTime.getTime() < start.getTime()) {
            return AjaxResult.error("考试未开始！");
        }
        //判断考试是否过期 +60000是以防系统自动提交时的时间已超过考试结束时间
        if(finishTime.getTime()>(end.getTime()+60000)){
            return AjaxResult.error("考试已结束！");
        }
        //判断学生是否已经交卷过
        /*Record stuRecord = new Record();
        stuRecord.setExamId(currentExam.getId());
        stuRecord.setStuId(currentStudent.getId());
        List<Record> records = recordDao.selectStudentRecordList(stuRecord);
        if (!records.isEmpty()) {
            return AjaxResult.error("请勿重复交卷！");
        }*/
        //自动评卷（选择题、判断题、填空题）  主观题需老师在线评卷
        String state = "完成";   //考试状态
        Double point = 0.0;   //记录学生得分
        TopicOfPaper papram = new TopicOfPaper();
        papram.setPaperId(currentExam.getPaper().getId());
        List<TopicOfPaper> topics = topicOfPaperService.selectTopicOfPaperList(papram);  //获取该试卷的所有原题及答案
        List<AnswerOfStudent> answerOfStudentList = new ArrayList<AnswerOfStudent>();  //记录学生每道题的答题情况
        for (int i = 0; i < vos.size(); i++) {
            ExamAnswerVo examAnswers = vos.get(i);   //当前题目的学生答案
            TopicOfPaper topicOfPaper = topics.get(i);    //当前对应的试卷原题，对比答案
            AnswerOfStudent answer = new AnswerOfStudent();
            answer.setPtId(topicOfPaper.getId());    //记录当前题目
            if (!ObjectUtils.isEmpty(examAnswers.getType())) {
                if (!"subjective".equals(examAnswers.getType())) {
                    if (!"fillEmpty".equals(examAnswers.getType())) {
                        answer.setAnswer(examAnswers.getAnswer());   //记录学生选择题答案
                        //选择题 判断题直接对比答案给分
                        if (examAnswers.getAnswer().equals(topicOfPaper.getTopic().getAnswer())) {
                            point += topicOfPaper.getScore();
                            answer.setIsRight("true");   //记录学生答题正确
                            answer.setScore(topicOfPaper.getScore());   //记录题目得分
                        } else {
                            answer.setIsRight("false");   //记录学生答题错误
                            answer.setScore(0.0);  //记录题目得分
                        }
                    } else {
                        //填空题需按空格给分
                        String fillEmptyRightAnswer = topicOfPaper.getTopic().getAnswer();
                        String regex = "[{]{2}[|][}]{2}";
                        String[] rightAnswers = fillEmptyRightAnswer.split(regex);
                        int answerCount = rightAnswers.length;  //填空题空格数
                        double answerScore = topicOfPaper.getScore() / answerCount;  //填空题每个空的分数，平均分配
                        StringBuilder studentFillEmptyAnswer = new StringBuilder();   //记录学生当前填空题所有答案
                        StringBuilder studentAnswerIsRight = new StringBuilder(90);    //记录学生当前填空题每个空格是否正确
                        double totalScore = 0.0;    //记录学生当前填空题的总得分
                        for (int j = 0; j < answerCount; j++) {
                            //按空格顺序遍历各个空的答案和学生填空答案
                            if (j == 0) {
                                if (examAnswers.getAnswer1().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer1()).append("{{|}}");
                                    studentAnswerIsRight.append("true、");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer1())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer1()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false、");
                                }
                            } else if (j == 1) {
                                if (examAnswers.getAnswer2().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer2()).append("{{|}}");
                                    studentAnswerIsRight.append("true、");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer2())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer2()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false、");
                                }
                            } else if (j == 2) {
                                if (examAnswers.getAnswer3().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer3()).append("{{|}}");
                                    studentAnswerIsRight.append("true、");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer3())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer3()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false、");
                                }
                            } else if (j == 3) {
                                if (examAnswers.getAnswer4().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer4()).append("{{|}}");
                                    studentAnswerIsRight.append("true、");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer4())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer4()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false、");
                                }
                            } else {
                                if (examAnswers.getAnswer5().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer5()).append("{{|}}");
                                    studentAnswerIsRight.append("true、");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer5())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer5()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false、");
                                }
                            }
                        }
                        answer.setAnswer(studentFillEmptyAnswer.toString());
                        answer.setIsRight(studentAnswerIsRight.toString());
                        answer.setScore(totalScore);
                    }
                } else {
                    //若有主观题 需老师在线评卷
                    answer.setAnswer(examAnswers.getAnswer());  //记录学生该主观题答案
                    state = "待评卷";
                }
            } else {
                return AjaxResult.error("数据异常");
            }
            answerOfStudentList.add(answer);   //保存学生当前题目答案
        }
        //保存考试记录
        Record record = new Record();
        String recordCode = ProjectUtil.getUuid();  //记录唯一码
        record.setStuId(currentStudent.getId());
        record.setExamId(currentExam.getId());
        record.setPoint(point);
        record.setState(state);
        record.setFinishTime(finishTime);
        record.setCode(recordCode);
        if (recordDao.insertRecord(record) > 0) {
            //保存学生答案
            Record r = new Record();
            r.setCode(recordCode);
            r.setStuId(currentStudent.getId());
            r.setExamId(currentExam.getId());
            //获得刚保存的考试记录Id
            Integer recordId = recordDao.selectRecordList(r).get(0).getId();
            for (AnswerOfStudent answerOfStudent : answerOfStudentList) {
                answerOfStudent.setRecordId(recordId);
                if (answerOfStudentDao.insertAnswerOfStudent(answerOfStudent) == 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("交卷程序发生异常，请稍后重试");
                }
            }
        } else {
            return AjaxResult.error("交卷程序发生异常，请稍后重试");
        }
        return AjaxResult.success("交卷成功！");
    }

    /**
     * 主观题老师评分业务
     */
    @Override
    @Transactional
    public AjaxResult markExam(Record record, Double[] score){
        List<AnswerOfStudent> answerList = answerOfStudentDao.selectSubjectiveAnswerList(record.getId());
        if(answerList.isEmpty()){
            return AjaxResult.error("数据异常");
        }
        if(score.length!=answerList.size()){
            return AjaxResult.error("数据异常");
        }
        String error = "";
        double subjectiveTotalPoint = 0.0;   //记录主观题总得分
        for (int i = 0; i < answerList.size(); i++) {
            AnswerOfStudent answerOfStudent = answerList.get(i);
            //判断前端输入的每道主观题分数是否合法，即比较每道主观题的分数和前端分数大小
            if(answerOfStudent.getTopic().getScore() < score[i]){
                error = "评分分数不能大于题目分数！";
                break;
            }else{
                if(score[i]<0){
                    //判断分数是否为负数
                    error = "评分不能为负数！";
                    break;
                }else {
                    //更新学生答案数据
                    answerOfStudent.setScore(score[i]);
                    if(answerOfStudentDao.updateAnswerOfStudent(answerOfStudent)==0){
                        error = "评卷程序发生异常！请稍后重试！";
                        break;
                    }
                    subjectiveTotalPoint+=score[i];
                }
            }
        }
        if(!"".equals(error)){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error(error);
        }
        //更新学生考试总分和考试状态
        record.setState("完成");
        double point = record.getPoint()+subjectiveTotalPoint;   //得到评卷后的考试总分
        record.setPoint(point);
        if(recordDao.updateRecord(record)==0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("评卷程序发生异常！请稍后重试！");
        }
        return AjaxResult.success("已完成评卷！");
    }
}
