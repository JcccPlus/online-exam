package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Paper;

import java.util.List;

public interface PaperService {
    List<Paper> selectPaperList(Paper paper);

    Paper selectPaperById(Integer id);

    boolean insertPaper(Paper paper);

    boolean updatePaper(Paper paper);

    boolean deletePaper(Paper paper);
}
