package cn.edu.hstc.service;

import cn.edu.hstc.pojo.College;

import java.util.List;

public interface CollegeService {

    College selectCollegeById(Integer id);

    List<College> selectCollegeList(College college);

    boolean insertCollege(College college);

    boolean updateCollege(College college);

    boolean deleteCollege(Integer id);
}
