package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.AnswerOfStudent;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Jc
 */
@Mapper
public interface AnswerOfStudentDao {

    List<AnswerOfStudent> selectAnswerOfStudentList(AnswerOfStudent answerOfStudent);

    int insertAnswerOfStudent(AnswerOfStudent answerOfStudent);

    int updateAnswerOfStudent(AnswerOfStudent answerOfStudent);

}
