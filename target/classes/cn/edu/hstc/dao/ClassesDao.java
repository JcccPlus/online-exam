package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Classes;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClassesDao {

    int insertClass(Classes classes);

    List<Classes> selectClasses(Classes classes);

    int updateClasses(Classes classes);

    int deleteClasses(Integer id);

}
