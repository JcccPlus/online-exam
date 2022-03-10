package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Exam;

import java.util.List;

public interface ExamService {
    List<Exam> selectExamList(Exam exam);

    Exam selectExamById(Integer id);

    boolean insertExam(Exam exam);

    boolean updateExam(Exam exam);

    boolean deleteExam(Exam exam);

    /**
     * 查找某个班级下，指定学生未考的考试
     */
    List<Exam> selectCurrentExam(Exam exam, Integer stuId);
}
