package cn.edu.hstc.service;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Paper;
import cn.edu.hstc.vo.AutomaticPaperVo;
import cn.edu.hstc.vo.ManualPaperVo;

import java.util.List;

public interface PaperService {
    List<Paper> selectPaperList(Paper paper);

    Paper selectPaperById(Integer id);

    boolean insertPaper(Paper paper);

    boolean updatePaper(Paper paper);

    boolean deletePaper(Paper paper);

    AjaxResult autoGeneratePaper(List<AutomaticPaperVo> vos);

    String downPaper(Paper paper);

    AjaxResult manuallyGeneratePaper(List<ManualPaperVo> vos);
}
