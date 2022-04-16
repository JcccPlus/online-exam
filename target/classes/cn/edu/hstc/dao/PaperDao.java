package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Paper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PaperDao {

    List<Paper> selectPaperList(Paper paper);

    Paper selectPaperById(Integer id);

    int insertPaper(Paper paper);

    int updatePaper(Paper paper);

    int deletePaper(Paper paper);

}
