package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.CollegeDao;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.service.CollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    private CollegeDao collegeDao;

    @Override
    public College selectCollegeById(Integer id) {
        return collegeDao.selectCollegeById(id);
    }

    @Override
    public List<College> selectCollegeList(College college) {
        return collegeDao.selectCollegeList(college);
    }

    @Override
    public boolean insertCollege(College college) {
        int row = collegeDao.insertCollege(college);
        return row > 0;
    }

    @Override
    public boolean updateCollege(College college) {
        int row = collegeDao.updateCollege(college);
        return row > 0;
    }

    @Override
    public boolean deleteCollege(Integer id) {
        int row = collegeDao.deleteCollege(id);
        return row > 0;
    }

}
