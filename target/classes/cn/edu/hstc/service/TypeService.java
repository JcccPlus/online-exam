package cn.edu.hstc.service;

import cn.edu.hstc.pojo.Type;
import java.util.List;

public interface TypeService {

    List<Type> selectTypeList(Type type);

    Type selectTypeById(Integer id);

}
