package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Stage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StageDao {

    List<Stage> selectStageList(Stage stage);

    Stage selectStageById(Integer id);

    int insertStage(Stage stage);

    int updateStage(Stage stage);

    int deleteStage(Stage stage);

}
