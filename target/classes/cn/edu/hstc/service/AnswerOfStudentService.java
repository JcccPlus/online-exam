package cn.edu.hstc.service;

import cn.edu.hstc.pojo.AnswerOfStudent;

import java.util.List;

public interface AnswerOfStudentService {

    List<AnswerOfStudent> selectAnswerOfStudentList(AnswerOfStudent answerOfStudent);

    boolean insertAnswerOfStudent(AnswerOfStudent answerOfStudent);

    boolean updateAnswerOfStudent(AnswerOfStudent answerOfStudent);

}
