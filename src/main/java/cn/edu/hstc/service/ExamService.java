package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Exam;

import java.util.List;

public interface ExamService {
    List<Exam> selectExamList(Exam exam);

    Exam selectExamById(Integer id);

    boolean insertExam(Exam exam);

    boolean updateExam(Exam exam);

    boolean deleteExam(Exam exam);
}
