package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Classes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassesDao {

    Classes selectClassById(Integer id);

    List<Classes> selectClassesList(Classes classes);

    int insertClass(Classes classes);

    int updateClasses(Classes classes);

    int deleteClass(Integer id);

    List<Classes> selectClassesByCollegeAndMajor(@Param("collegeName") String collegeName, @Param("majorName") String majorName);

    List<Classes> selectClassesByCollegeId(@Param("collegeId") Integer collegeId);

}
