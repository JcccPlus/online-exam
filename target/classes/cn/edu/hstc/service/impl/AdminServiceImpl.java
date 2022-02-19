package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.AdminDao;
import cn.edu.hstc.pojo.Admin;
import cn.edu.hstc.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminDao adminDao;

    @Override
    public Admin getAdmin(String username, String password) {
        return adminDao.getAdmin(username, password);
    }
}
