package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Exam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ExamDao {

    List<Exam> selectExamList(Exam exam);

    Exam selectExamById(Integer id);

    int insertExam(Exam exam);

    int updateExam(Exam exam);

    int deleteExam(Exam exam);

    /**
     * 查找某个班级下，指定学生未考的考试
     */
    List<Exam> selectCurrentExam(@Param("exam") Exam exam, @Param("stuId") Integer stuId);

}
