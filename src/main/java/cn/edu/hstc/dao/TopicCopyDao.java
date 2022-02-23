package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.TopicCopy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TopicCopyDao {

    TopicCopy selectTopicCopyById(Integer id);

    List<TopicCopy> selectTopicCopyList(TopicCopy topicCopy);

    int insertTopicCopy(TopicCopy topicCopy);

}
