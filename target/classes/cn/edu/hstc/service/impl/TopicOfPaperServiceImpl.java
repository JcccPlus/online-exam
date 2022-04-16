package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.TopicOfPaperDao;
import cn.edu.hstc.pojo.TopicOfPaper;
import cn.edu.hstc.service.TopicOfPaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicOfPaperServiceImpl implements TopicOfPaperService {
    @Autowired
    private TopicOfPaperDao topicOfPaperDao;

    @Override
    public List<TopicOfPaper> selectTopicOfPaperList(TopicOfPaper topicOfPaper) {
        return topicOfPaperDao.selectTopicOfPaperList(topicOfPaper);
    }

    @Override
    public TopicOfPaper selectTopicOfPaperById(Integer id) {
        return topicOfPaperDao.selectTopicOfPaperById(id);
    }

    @Override
    public int insertTopicOfPaper(TopicOfPaper topicOfPaper) {
        return topicOfPaperDao.insertTopicOfPaper(topicOfPaper);
    }
}
