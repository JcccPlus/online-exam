package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.College;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollegeDao {

    College selectCollegeById(Integer id);

    List<College> selectCollegeList(College college);

    int insertCollege(College college);

    int updateCollege(College college);

    int deleteCollege(Integer id);

}
