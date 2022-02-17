package cn.edu.hstc.dao;

import cn.edu.hstc.domain.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
public interface TestDao {

    @Select("select * from tb_admin where id = #{id}")
    Admin getAdminById(@Param("id") int id);

}
