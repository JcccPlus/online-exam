package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.PaperDao;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.pojo.Paper;
import cn.edu.hstc.service.PaperService;
import cn.edu.hstc.util.ProjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaperServiceImpl implements PaperService {
    @Autowired
    private PaperDao paperDao;

    @Override
    public List<Paper> selectPaperList(Paper paper) {
        return paperDao.selectPaperList(paper);
    }

    @Override
    public Paper selectPaperById(Integer id) {
        return paperDao.selectPaperById(id);
    }

    @Override
    public boolean insertPaper(Paper paper) {
        paper.setCode(ProjectUtil.getUuid().substring(0, 16));
        paper.setCreateTime(DateUtils.getNowDate());
        return paperDao.insertPaper(paper) > 0;
    }

    @Override
    public boolean updatePaper(Paper paper) {
        paper.setUpdateTime(DateUtils.getNowDate());
        return paperDao.updatePaper(paper) > 0;
    }

    @Override
    public boolean deletePaper(Paper paper) {
        return paperDao.deletePaper(paper) > 0;
    }
}
