package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Record;

import java.util.List;

public interface RecordService {
    List<Record> selectRecordList(Record record);

    Record selectRecordById(Integer id);

    boolean insertRecord(Record record);

    /**
     * 检查考试是否过期，过期的话是否有缺考学生，有缺考学生需要新增缺考学生的考试记录
     */
    boolean hasStudentMissingExam(Exam exam);

    /**
     * 查找某个学生的所有的考试记录
     */
    List<Record> selectStudentRecordList(Record record);
}
