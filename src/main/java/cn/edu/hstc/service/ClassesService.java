package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Classes;

import java.util.List;

public interface ClassesService {

    Classes selectClassById(Integer id);

    List<Classes> selectClassesList(Classes classes);

    boolean insertClass(Classes classes);

    boolean updateClasses(Classes classes);

    boolean deleteClass(Integer id);

    List<Classes> selectClassesByCollegeAndMajor(String collegeName, String majorName);

    List<Classes> selectClassesByCollegeId(Integer collegeId);

}
