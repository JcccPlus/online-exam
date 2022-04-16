package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Stats;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StatsDao {

    List<Stats> selectStatsList(Stats stats);

    int insertStats(Stats stats);

    int updateStats(Stats stats);

}
