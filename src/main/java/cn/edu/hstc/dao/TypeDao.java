package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TypeDao {

    List<Type> selectTypeList(Type type);

    Type selectTypeById(Integer id);

}
