package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Record;
import cn.edu.hstc.pojo.Stats;

import java.util.List;

public interface StatsService {

    List<Stats> selectStatsList(Stats stats);

    boolean insertStats(Stats stats);

    boolean updateStats(Stats stats);

    /**
     * 统计考试过程中的学生考试情况
     */
    Stats getTheExamProcessStats(Exam exam, List<Record> records, long classTotal);

    /**
     * 统计考试结束后的学生考试情况
     */
    Stats getTheStats(Exam exam, List<Record> records);

}
