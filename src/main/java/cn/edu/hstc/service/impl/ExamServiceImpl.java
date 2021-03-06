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
            return AjaxResult.error("????????????");
        }
        Object user = ServletUtils.getSession().getAttribute("user");
        if (!(user instanceof Student)) {
            return AjaxResult.error("???????????????");
        }
        Student currentStudent = (Student) user;
        Date finishTime = new Date(); //????????????????????????
        Exam param = new Exam();
        param.setCode(code);  //???????????????
        param.setClassId(currentStudent.getClassId());  //????????????   //????????????
        List<Exam> list = selectExamList(param);
        if(list.isEmpty()){
            //???????????????????????????????????????????????????????????????????????????
            return AjaxResult.error("????????????");
        }
        List<Exam> exams = selectCurrentExam(param, currentStudent.getId());
        if (exams.isEmpty()) {
            //????????????????????????
            return AjaxResult.error("?????????????????????");
        }
        Exam currentExam = exams.get(0);  //????????????????????????
        Date start = currentExam.getStart();
        Date end = currentExam.getEnd();
        //????????????????????????
        if (finishTime.getTime() < start.getTime()) {
            return AjaxResult.error("??????????????????");
        }
        //???????????????????????? +60000??????????????????????????????????????????????????????????????????
        if(finishTime.getTime()>(end.getTime()+60000)){
            return AjaxResult.error("??????????????????");
        }
        //?????????????????????????????????
        /*Record stuRecord = new Record();
        stuRecord.setExamId(currentExam.getId());
        stuRecord.setStuId(currentStudent.getId());
        List<Record> records = recordDao.selectStudentRecordList(stuRecord);
        if (!records.isEmpty()) {
            return AjaxResult.error("?????????????????????");
        }*/
        //???????????????????????????????????????????????????  ??????????????????????????????
        String state = "??????";   //????????????
        Double point = 0.0;   //??????????????????
        TopicOfPaper papram = new TopicOfPaper();
        papram.setPaperId(currentExam.getPaper().getId());
        List<TopicOfPaper> topics = topicOfPaperService.selectTopicOfPaperList(papram);  //???????????????????????????????????????
        List<AnswerOfStudent> answerOfStudentList = new ArrayList<AnswerOfStudent>();  //????????????????????????????????????
        for (int i = 0; i < vos.size(); i++) {
            ExamAnswerVo examAnswers = vos.get(i);   //???????????????????????????
            TopicOfPaper topicOfPaper = topics.get(i);    //??????????????????????????????????????????
            AnswerOfStudent answer = new AnswerOfStudent();
            answer.setPtId(topicOfPaper.getId());    //??????????????????
            if (!ObjectUtils.isEmpty(examAnswers.getType())) {
                if (!"subjective".equals(examAnswers.getType())) {
                    if (!"fillEmpty".equals(examAnswers.getType())) {
                        answer.setAnswer(examAnswers.getAnswer());   //???????????????????????????
                        //????????? ?????????????????????????????????
                        if (examAnswers.getAnswer().equals(topicOfPaper.getTopic().getAnswer())) {
                            point += topicOfPaper.getScore();
                            answer.setIsRight("true");   //????????????????????????
                            answer.setScore(topicOfPaper.getScore());   //??????????????????
                        } else {
                            answer.setIsRight("false");   //????????????????????????
                            answer.setScore(0.0);  //??????????????????
                        }
                    } else {
                        //???????????????????????????
                        String fillEmptyRightAnswer = topicOfPaper.getTopic().getAnswer();
                        String regex = "[{]{2}[|][}]{2}";
                        String[] rightAnswers = fillEmptyRightAnswer.split(regex);
                        int answerCount = rightAnswers.length;  //??????????????????
                        double answerScore = topicOfPaper.getScore() / answerCount;  //??????????????????????????????????????????
                        StringBuilder studentFillEmptyAnswer = new StringBuilder();   //???????????????????????????????????????
                        StringBuilder studentAnswerIsRight = new StringBuilder(90);    //???????????????????????????????????????????????????
                        double totalScore = 0.0;    //???????????????????????????????????????
                        for (int j = 0; j < answerCount; j++) {
                            //????????????????????????????????????????????????????????????
                            if (j == 0) {
                                if (examAnswers.getAnswer1().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer1()).append("{{|}}");
                                    studentAnswerIsRight.append("true???");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer1())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer1()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false???");
                                }
                            } else if (j == 1) {
                                if (examAnswers.getAnswer2().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer2()).append("{{|}}");
                                    studentAnswerIsRight.append("true???");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer2())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer2()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false???");
                                }
                            } else if (j == 2) {
                                if (examAnswers.getAnswer3().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer3()).append("{{|}}");
                                    studentAnswerIsRight.append("true???");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer3())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer3()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false???");
                                }
                            } else if (j == 3) {
                                if (examAnswers.getAnswer4().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer4()).append("{{|}}");
                                    studentAnswerIsRight.append("true???");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer4())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer4()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false???");
                                }
                            } else {
                                if (examAnswers.getAnswer5().equals(rightAnswers[j])) {
                                    point += answerScore;
                                    studentFillEmptyAnswer.append(examAnswers.getAnswer5()).append("{{|}}");
                                    studentAnswerIsRight.append("true???");
                                    totalScore += answerScore;
                                } else {
                                    if (!ObjectUtils.isEmpty(examAnswers.getAnswer5())) {
                                        studentFillEmptyAnswer.append(examAnswers.getAnswer5()).append("{{|}}");
                                    } else {
                                        studentFillEmptyAnswer.append("{{null}}{{|}}");
                                    }
                                    studentAnswerIsRight.append("false???");
                                }
                            }
                        }
                        answer.setAnswer(studentFillEmptyAnswer.toString());
                        answer.setIsRight(studentAnswerIsRight.toString());
                        answer.setScore(totalScore);
                    }
                } else {
                    //??????????????? ?????????????????????
                    answer.setAnswer(examAnswers.getAnswer());  //??????????????????????????????
                    state = "?????????";
                }
            } else {
                return AjaxResult.error("????????????");
            }
            answerOfStudentList.add(answer);   //??????????????????????????????
        }
        //??????????????????
        Record record = new Record();
        String recordCode = ProjectUtil.getUuid();  //???????????????
        record.setStuId(currentStudent.getId());
        record.setExamId(currentExam.getId());
        record.setPoint(point);
        record.setState(state);
        record.setFinishTime(finishTime);
        record.setCode(recordCode);
        if (recordDao.insertRecord(record) > 0) {
            //??????????????????
            Record r = new Record();
            r.setCode(recordCode);
            r.setStuId(currentStudent.getId());
            r.setExamId(currentExam.getId());
            //??????????????????????????????Id
            Integer recordId = recordDao.selectRecordList(r).get(0).getId();
            for (AnswerOfStudent answerOfStudent : answerOfStudentList) {
                answerOfStudent.setRecordId(recordId);
                if (answerOfStudentDao.insertAnswerOfStudent(answerOfStudent) == 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("??????????????????????????????????????????");
                }
            }
        } else {
            return AjaxResult.error("??????????????????????????????????????????");
        }
        return AjaxResult.success("???????????????");
    }

    /**
     * ???????????????????????????
     */
    @Override
    @Transactional
    public AjaxResult markExam(Record record, Double[] score){
        List<AnswerOfStudent> answerList = answerOfStudentDao.selectSubjectiveAnswerList(record.getId());
        if(answerList.isEmpty()){
            return AjaxResult.error("????????????");
        }
        if(score.length!=answerList.size()){
            return AjaxResult.error("????????????");
        }
        String error = "";
        double subjectiveTotalPoint = 0.0;   //????????????????????????
        for (int i = 0; i < answerList.size(); i++) {
            AnswerOfStudent answerOfStudent = answerList.get(i);
            //???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if(answerOfStudent.getTopic().getScore() < score[i]){
                error = "???????????????????????????????????????";
                break;
            }else{
                if(score[i]<0){
                    //???????????????????????????
                    error = "????????????????????????";
                    break;
                }else {
                    //????????????????????????
                    answerOfStudent.setScore(score[i]);
                    if(answerOfStudentDao.updateAnswerOfStudent(answerOfStudent)==0){
                        error = "?????????????????????????????????????????????";
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
        //???????????????????????????????????????
        record.setState("??????");
        double point = record.getPoint()+subjectiveTotalPoint;   //??????????????????????????????
        record.setPoint(point);
        if(recordDao.updateRecord(record)==0){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("?????????????????????????????????????????????");
        }
        return AjaxResult.success("??????????????????");
    }
}
