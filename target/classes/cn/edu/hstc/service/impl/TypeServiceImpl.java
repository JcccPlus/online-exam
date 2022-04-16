package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.TypeDao;
import cn.edu.hstc.pojo.Type;
import cn.edu.hstc.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeDao typeDao;

    @Override
    public List<Type> selectTypeList(Type type) {
        return typeDao.selectTypeList(type);
    }

    @Override
    public Type selectTypeById(Integer id) {
        return typeDao.selectTypeById(id);
    }
}
