package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.ClassesDao;
import cn.edu.hstc.pojo.Classes;
import cn.edu.hstc.service.ClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassesServiceImpl implements ClassesService {
    @Autowired
    private ClassesDao classesDao;

    @Override
    public Classes selectClassById(Integer id) {
        return classesDao.selectClassById(id);
    }

    @Override
    public List<Classes> selectClassesList(Classes classes) {
        return classesDao.selectClassesList(classes);
    }

    @Override
    public boolean insertClass(Classes classes) {
        return classesDao.insertClass(classes) > 0;
    }

    @Override
    public boolean updateClasses(Classes classes) {
        return classesDao.updateClasses(classes) > 0;
    }

    @Override
    public boolean deleteClass(Integer id) {
        return classesDao.deleteClass(id) > 0;
    }

    @Override
    public List<Classes> selectClassesByCollegeAndMajor(String collegeName, String majorName) {
        return classesDao.selectClassesByCollegeAndMajor(collegeName, majorName);
    }

    @Override
    public List<Classes> selectClassesByCollegeId(Integer collegeId) {
        return classesDao.selectClassesByCollegeId(collegeId);
    }
}
