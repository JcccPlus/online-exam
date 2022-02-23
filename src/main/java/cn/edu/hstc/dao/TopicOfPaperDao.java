package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.TopicOfPaper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TopicOfPaperDao {

    List<TopicOfPaper> selectTopicOfPaperList(TopicOfPaper topicOfPaper);

    TopicOfPaper selectTopicOfPaperById(Integer id);

    int insertTopicOfPaper(TopicOfPaper topicOfPaper);

}
