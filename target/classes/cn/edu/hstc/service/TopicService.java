package cn.edu.hstc.service;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.vo.TopicVo;
import cn.edu.hstc.pojo.Topic;

import java.util.List;

public interface TopicService {
    List<Topic> selectTopicList(Topic topic);

    Topic selectTopicById(Integer id);

    boolean insertTopic(Topic topic);

    boolean updateTopic(Topic topic);

    boolean deleteTopic(Topic topic);

    List<Topic> selectTopicListByPo(TopicVo topicVo);

    AjaxResult insertMoreTopic(List<Topic> topics);
}
