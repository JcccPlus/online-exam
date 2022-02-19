package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Major;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MajorDao {

    int insertMajor(Major major);

    List<Major> selecMajors(Major major);

    int updateMajor(Major major);

    int deleteMajor(Integer id);

}
