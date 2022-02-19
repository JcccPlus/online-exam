package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author Jc
 */
@Mapper
public interface AdminDao {

    Admin getAdmin(@Param("username") String username,@Param("password") String password);

}
