package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Admin;

public interface AdminService {
    Admin getAdmin(String username,String password);
}
