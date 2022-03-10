package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.RecordDao;
import cn.edu.hstc.dao.StudentDao;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Record;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.service.RecordService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Date;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    private RecordDao recordDao;
    @Autowired
    private StudentDao studentDao;

    @Override
    public List<Record> selectRecordList(Record record) {
        return recordDao.selectRecordList(record);
    }

    @Override
    public Record selectRecordById(Integer id) {
        return recordDao.selectRecordById(id);
    }

    @Override
    public boolean insertRecord(Record record) {
        record.setCode(ProjectUtil.getUuid());
        record.setFinishTime(DateUtils.getNowDate());
        return recordDao.insertRecord(record) > 0;
    }

    /**
     * 检查考试是否过期，过期的话是否有缺考学生，有缺考学生需要新增缺考学生的考试记录
     */
    @Override
    @Transactional
    public boolean hasStudentMissingExam(Exam exam) {
        Date now = new Date();
        Date end = exam.getEnd();
        if (now.getTime() > end.getTime()) {
            //考试已过期，是否有缺考学生
            List<Student> students = studentDao.selectStudentsOfMissingExam(exam);
            if (!students.isEmpty()) {
                //有缺考学生需要新增缺考学生的考试记录
                for (Student student : students) {
                    Record record = new Record();
                    record.setCode(ProjectUtil.getUuid());
                    record.setExamId(exam.getId());
                    record.setStuId(student.getId());
                    record.setState("缺考");
                    record.setPoint(0.0);
                    if (recordDao.insertRecord(record) == 0) {
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<Record> selectStudentRecordList(Record record) {
        return recordDao.selectStudentRecordList(record);
    }
}
