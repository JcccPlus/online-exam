package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Exam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ExamDao {

    List<Exam> selectExamList(Exam exam);

    Exam selectExamById(Integer id);

    int insertExam(Exam exam);

    int updateExam(Exam exam);

    int deleteExam(Exam exam);

}
