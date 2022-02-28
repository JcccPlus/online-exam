package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.StageDao;
import cn.edu.hstc.pojo.Stage;
import cn.edu.hstc.service.StageService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StageServiceImpl implements StageService {
    @Autowired
    private StageDao stageDao;

    @Override
    public List<Stage> selectStageList(Stage stage) {
        return stageDao.selectStageList(stage);
    }

    @Override
    public Stage selectStageById(Integer id) {
        return stageDao.selectStageById(id);
    }

    @Override
    public boolean insertStage(Stage stage) {
        stage.setCode(ProjectUtil.getUuid().substring(0, 16));
        return stageDao.insertStage(stage) > 0;
    }

    @Override
    public boolean updateStage(Stage stage) {
        return stageDao.updateStage(stage) > 0;
    }

    @Override
    public boolean deleteStage(Stage stage) {
        return stageDao.deleteStage(stage) > 0;
    }
}
