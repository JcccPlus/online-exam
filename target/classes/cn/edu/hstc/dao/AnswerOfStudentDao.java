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

    /**
     * 获取指定‘待评卷’考试记录的所有主观题答题数据
     */
    List<AnswerOfStudent> selectSubjectiveAnswerList(Integer recordId);

}
