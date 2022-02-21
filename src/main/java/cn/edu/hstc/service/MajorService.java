package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Major;

import java.util.List;

public interface MajorService {

    Major selectMajorById(Integer id);

    List<Major> selectMajorList(Major major);

    boolean insertMajor(Major major);

    boolean updateMajor(Major major);

    boolean deleteMajor(Integer id);

    List<Major> selectMajorsByCollege(String name);

}
