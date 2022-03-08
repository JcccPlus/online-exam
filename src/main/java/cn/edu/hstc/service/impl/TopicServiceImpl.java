package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.TopicCopyDao;
import cn.edu.hstc.dao.TopicDao;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.vo.TopicVo;
import cn.edu.hstc.pojo.Topic;
import cn.edu.hstc.pojo.TopicCopy;
import cn.edu.hstc.service.TopicService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {
    @Autowired
    private TopicDao topicDao;
    @Autowired
    private TopicCopyDao topicCopyDao;

    @Override
    public List<Topic> selectTopicList(Topic topic) {
        return topicDao.selectTopicList(topic);
    }

    @Override
    public Topic selectTopicById(Integer id) {
        return topicDao.selectTopicById(id);
    }

    @Override
    public boolean insertTopic(Topic topic) {
        String code = ProjectUtil.getUuid();
        Date date = DateUtils.getNowDate();
        //题目缓存
        TopicCopy topicCopy = new TopicCopy();
        topicCopy.setTitle(topic.getTitle());
        topicCopy.setOpts(topic.getOpts());
        topicCopy.setAnswer(topic.getAnswer());
        topicCopy.setAnalysis(topic.getAnalysis());
        topicCopy.setTypeId(topic.getTypeId());
        topicCopy.setLevelId(topic.getLevelId());
        topicCopy.setStageId(topic.getStageId());
        topicCopy.setCode(code);
        topicCopy.setCreateTime(date);
        topicCopy.setRemark(topic.getRemark());
        if (topicCopyDao.insertTopicCopy(topicCopy) == 0) {
            return false;
        }
        topic.setCode(code);
        topic.setCreateTime(date);
        return topicDao.insertTopic(topic) > 0;
    }

    @Override
    public boolean updateTopic(Topic topic) {
        Date date = DateUtils.getNowDate();
        //更新题目缓存
        TopicCopy topicCopy = new TopicCopy();
        topicCopy.setTitle(topic.getTitle());
        topicCopy.setOpts(topic.getOpts());
        topicCopy.setAnswer(topic.getAnswer());
        topicCopy.setAnalysis(topic.getAnalysis());
        topicCopy.setTypeId(topic.getTypeId());
        topicCopy.setLevelId(topic.getLevelId());
        topicCopy.setStageId(topic.getStageId());
        topicCopy.setCode(topic.getCode());
        topicCopy.setUpdateTime(date);
        topicCopy.setRemark(topic.getRemark());
        if (topicCopyDao.updateTopicCopy(topicCopy) == 0) {
            return false;
        }
        topic.setUpdateTime(date);
        return topicDao.updateTopic(topic) > 0;
    }

    @Override
    public boolean deleteTopic(Topic topic) {
        return topicDao.deleteTopic(topic) > 0;
    }

    @Override
    public List<Topic> selectTopicListByPo(TopicVo topicVo) {
        return topicDao.selectTopicListByPo(topicVo);
    }

    @Override
    @Transactional
    public AjaxResult insertMoreTopic(List<Topic> topics) {
        int rows = 0;
        for (int i = 0; i < topics.size(); i++) {
            Topic topic = topics.get(i);
            if (insertTopic(topic)) {
                rows += 1;
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return AjaxResult.error("题目添加失败，请稍后再试！");
            }
        }
        if (rows > 0) {
            return AjaxResult.success("成功添加" + rows + "道题！");
        }
        return AjaxResult.error();
    }

}
