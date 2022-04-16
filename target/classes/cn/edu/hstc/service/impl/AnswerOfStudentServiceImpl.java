package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.AnswerOfStudentDao;
import cn.edu.hstc.pojo.AnswerOfStudent;
import cn.edu.hstc.service.AnswerOfStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerOfStudentServiceImpl implements AnswerOfStudentService {
    @Autowired
    private AnswerOfStudentDao answerOfStudentDao;

    @Override
    public List<AnswerOfStudent> selectAnswerOfStudentList(AnswerOfStudent answerOfStudent) {
        return answerOfStudentDao.selectAnswerOfStudentList(answerOfStudent);
    }

    @Override
    public boolean insertAnswerOfStudent(AnswerOfStudent answerOfStudent) {
        return answerOfStudentDao.insertAnswerOfStudent(answerOfStudent)>0;
    }

    @Override
    public boolean updateAnswerOfStudent(AnswerOfStudent answerOfStudent) {
        return answerOfStudentDao.updateAnswerOfStudent(answerOfStudent)>0;
    }
}
