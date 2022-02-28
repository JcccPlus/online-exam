package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Stage;

import java.util.List;

public interface StageService {
    List<Stage> selectStageList(Stage stage);

    Stage selectStageById(Integer id);

    boolean insertStage(Stage stage);

    boolean updateStage(Stage stage);

    boolean deleteStage(Stage stage);
}
