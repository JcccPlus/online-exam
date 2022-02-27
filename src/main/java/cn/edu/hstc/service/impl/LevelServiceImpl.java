package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.LevelDao;
import cn.edu.hstc.pojo.Level;
import cn.edu.hstc.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelServiceImpl implements LevelService {
    @Autowired
    private LevelDao levelDao;

    @Override
    public List<Level> selectLevelList(Level level) {
        return levelDao.selectLevelList(level);
    }

    @Override
    public Level selectLevelById(Integer id) {
        return levelDao.selectLevelById(id);
    }
}
