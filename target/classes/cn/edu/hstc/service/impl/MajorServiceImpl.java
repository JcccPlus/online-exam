package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.MajorDao;
import cn.edu.hstc.pojo.Major;
import cn.edu.hstc.service.MajorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MajorServiceImpl implements MajorService {
    @Autowired
    private MajorDao majorDao;

    @Override
    public Major selectMajorById(Integer id) {
        return majorDao.selectMajorById(id);
    }

    @Override
    public List<Major> selectMajorList(Major major) {
        return majorDao.selectMajorList(major);
    }

    @Override
    public boolean insertMajor(Major major) {
        int row = majorDao.insertMajor(major);
        return row > 0;
    }

    @Override
    public boolean updateMajor(Major major) {
        int row = majorDao.updateMajor(major);
        return row > 0;
    }

    @Override
    public boolean deleteMajor(Integer id) {
        int row = majorDao.deleteMajor(id);
        return row > 0;
    }

    @Override
    public List<Major> selectMajorsByCollege(String name) {
        return majorDao.selectMajorsByCollege(name);
    }
}
