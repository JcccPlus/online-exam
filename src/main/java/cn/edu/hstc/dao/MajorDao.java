package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Major;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorDao {

    Major selectMajorById(Integer id);

    List<Major> selectMajorList(Major major);

    int insertMajor(Major major);

    int updateMajor(Major major);

    int deleteMajor(Integer id);

    List<Major> selectMajorsByCollege(String name);

}
