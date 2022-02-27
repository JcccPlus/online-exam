package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.ExamDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {
    @Autowired
    private ExamDao examDao;

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
        exam.setCode(ProjectUtil.getUuid().substring(0, 16));
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

}
