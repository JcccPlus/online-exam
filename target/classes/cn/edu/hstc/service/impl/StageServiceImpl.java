package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.CourseDao;
import cn.edu.hstc.dao.StageDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Stage;
import cn.edu.hstc.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class StageServiceImpl implements StageService {
    @Autowired
    private StageDao stageDao;
    @Autowired
    private CourseDao courseDao;

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

    @Override
    @Transactional
    public AjaxResult insertMoreStage(List<Stage> stages) {
        int rows = 0;
        for (int i = 0; i < stages.size(); i++) {
            Stage stage = stages.get(i);
            if (ObjectUtils.isEmpty(stage.getCourseId()) || ObjectUtils.isEmpty(stage.getCode())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return AjaxResult.error("数据异常，请稍后重试");
            }
            if (ObjectUtils.isEmpty(stage.getName())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return AjaxResult.error("第" + (i + 1) + "个阶段的名称为空，请重新输入！");
            }
            rows += stageDao.insertStage(stage);
        }
        if (rows > 0) {
            Stage stage = stages.get(0);
            String courseName = courseDao.selectCourseById(stage.getCourseId()).getName();
            return AjaxResult.success("成功为《" + courseName +"》添加"+ rows + "个阶段！");
        }
        return AjaxResult.error();
    }
}
