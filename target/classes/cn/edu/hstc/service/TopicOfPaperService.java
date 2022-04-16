package cn.edu.hstc.service;

import cn.edu.hstc.pojo.TopicOfPaper;

import java.util.List;

public interface TopicOfPaperService {

    List<TopicOfPaper> selectTopicOfPaperList(TopicOfPaper topicOfPaper);

    TopicOfPaper selectTopicOfPaperById(Integer id);

    int insertTopicOfPaper(TopicOfPaper topicOfPaper);
}
