package cn.edu.hstc.dao;

import cn.edu.hstc.vo.TopicVo;
import cn.edu.hstc.pojo.Topic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TopicDao {

    List<Topic> selectTopicList(Topic topic);

    Topic selectTopicById(Integer id);

    int insertTopic(Topic topic);

    int updateTopic(Topic topic);

    int deleteTopic(Topic topic);

    List<Topic> selectTopicListByPo(TopicVo topicVo);

}
