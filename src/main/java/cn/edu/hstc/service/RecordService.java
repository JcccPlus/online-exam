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
     * @return 程序执行结果 error程序异常 true存在缺考学生 false不存在缺考学生
     */
    String hasStudentMissingExam(Exam exam);

    /**
     * 查找某个学生的所有的考试记录
     */
    List<Record> selectStudentRecordList(Record record);
}
