package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Level;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LevelDao {

    List<Level> selectLevelList(Level level);

    Level selectLevelById(Integer id);

}
