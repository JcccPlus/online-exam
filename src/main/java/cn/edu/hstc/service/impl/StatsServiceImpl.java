package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.StatsDao;
import cn.edu.hstc.pojo.Exam;
import cn.edu.hstc.pojo.Record;
import cn.edu.hstc.pojo.Stats;
import cn.edu.hstc.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsServiceImpl implements StatsService {
    @Autowired
    private StatsDao statsDao;

    @Override
    public List<Stats> selectStatsList(Stats stats) {
        return statsDao.selectStatsList(stats);
    }

    @Override
    public boolean insertStats(Stats stats) {
        return statsDao.insertStats(stats) > 0;
    }

    @Override
    public boolean updateStats(Stats stats) {
        return statsDao.updateStats(stats) > 0;
    }

    @Override
    public Stats getTheStats(Exam exam, List<Record> records) {
        //平均分 中位数 最低分 最高分 优秀人数 良好人数 不及格人数 参加人数 缺考人数（涉及持久层）
        int size = records.size();
        int turnoutNum = 0;  //参加考试人数
        int absenceNum = 0;   //缺考人数
        double topScore = records.get(0).getPoint();   //最高分
        double lowScore = records.get(size-1).getPoint();   //最低分
        double medianScore = 0.0;   //中位数
        if (size % 2 == 0) {
            medianScore = (records.get((size / 2) - 1).getPoint() + records.get(size / 2).getPoint()) / 2;
        } else {
            medianScore = records.get((size / 2)).getPoint();
        }
        double examScore = exam.getScore();   //考试总分
        double passScore = examScore * 0.6;  //及格线
        double goodScore = examScore * 0.9;  //优秀线
        double medScore = examScore * 0.8;  //良好线
        int notPassNum = 0;  //不及格人数
        int goodNum = 0;   //优秀人数
        int medNum = 0;   //良好人数
        int norNum = 0;   //不好不差人数
        double classTotalScore = 0;   //班级总分
        String flag = "否";
        for (Record stuRecord : records) {
            double point = stuRecord.getPoint();
            classTotalScore += point;
            if (point >= goodScore) {
                goodNum += 1;
            } else if (point >= medScore) {
                medNum += 1;
            } else if (point >= passScore) {
                norNum += 1;
            } else {
                notPassNum += 1;
            }
            if (stuRecord.getState().equals("缺考")) {
                absenceNum += 1;
            } else {
                turnoutNum += 1;
            }
            if(stuRecord.getState().equals("待评卷")){
                flag = "是";
            }
        }
        double averageScore = classTotalScore / size;  // 平均分=班级总分/班级人数
        Stats currentStats = new Stats();
        currentStats.setTurnout(turnoutNum);
        currentStats.setAbsence(absenceNum);
        currentStats.setSTop(topScore);
        currentStats.setSLow(lowScore);
        currentStats.setSMedian(medianScore);
        currentStats.setGood(goodNum);
        currentStats.setMed(medNum);
        currentStats.setNor(norNum);
        currentStats.setBad(notPassNum);
        currentStats.setSAverage(averageScore);
        //currentStats.setExamId(exam.getId());
        //currentStats.setFlag(flag);
        currentStats.setPassScore(passScore);
        currentStats.setGoodScore(goodScore);
        currentStats.setMedScore(medScore);
        return currentStats;
    }

    @Override
    public Stats getTheExamProcessStats(Exam exam, List<Record> records, long classTotal){
        int turnoutNum = records.size();  //已交卷人数
        int absenceNum = (int) (classTotal - turnoutNum);   //正在考试人数
        double topScore = records.get(0).getPoint();   //最高分
        double lowScore = records.get(turnoutNum - 1).getPoint();   //最低分
        double medianScore = 0.0;   //中位数
        if (turnoutNum % 2 == 0) {
            medianScore = (records.get((turnoutNum / 2) - 1).getPoint() + records.get(turnoutNum / 2).getPoint()) / 2;
        } else {
            medianScore = records.get((turnoutNum / 2)).getPoint();
        }
        double examScore = exam.getScore();   //考试总分
        double passScore = examScore * 0.6;  //及格线
        double goodScore = examScore * 0.9;  //优秀线
        double medScore = examScore * 0.8;  //良好线
        int notPassNum = 0;  //不及格人数
        int goodNum = 0;   //优秀人数
        int medNum = 0;   //良好人数
        int norNum = 0;   //不好不差人数
        double classTotalScore = 0;   //班级总分
        for (Record stuRecord : records) {
            double point = stuRecord.getPoint();
            classTotalScore += point;
            if (point >= goodScore) {
                goodNum += 1;
            } else if (point >= medScore) {
                medNum += 1;
            } else if (point >= passScore) {
                norNum += 1;
            } else {
                notPassNum += 1;
            }
        }
        double averageScore = classTotalScore / turnoutNum;  // 平均分=班级总分/已参加考试的人数
        Stats currentStats = new Stats();
        currentStats.setTurnout(turnoutNum);
        currentStats.setAbsence(absenceNum);
        currentStats.setSTop(topScore);
        currentStats.setSLow(lowScore);
        currentStats.setSMedian(medianScore);
        currentStats.setGood(goodNum);
        currentStats.setMed(medNum);
        currentStats.setNor(norNum);
        currentStats.setBad(notPassNum);
        currentStats.setSAverage(averageScore);
        //currentStats.setExamId(exam.getId());
        currentStats.setPassScore(passScore);
        currentStats.setGoodScore(goodScore);
        currentStats.setMedScore(medScore);
        return currentStats;
    }
}
