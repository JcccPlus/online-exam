package cn.edu.hstc.dao;

import cn.edu.hstc.pojo.Record;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecordDao {

    List<Record> selectRecordList(Record record);

    Record selectRecordById(Integer id);

    int insertRecord(Record record);

}
