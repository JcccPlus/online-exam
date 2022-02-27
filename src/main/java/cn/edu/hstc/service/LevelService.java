package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Level;
import java.util.List;

public interface LevelService {

    List<Level> selectLevelList(Level level);

    Level selectLevelById(Integer id);
}
