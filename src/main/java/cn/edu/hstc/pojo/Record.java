package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 考试记录实体类tb_record
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record extends BaseEntity implements Comparable {
    /** 主键 */
    private Integer id;
    /** 所属考试 */
    private Integer examId;
    /** 所属学生 */
    private Integer stuId;
    /** 学生考试得分 */
    private Double point;
    /** 交卷时间 */
    private Date finishTime;
    /** 评卷状态 */
    private String state;
    /** 唯一码 */
    private String code;
    /** 考试实体 */
    private Exam exam;
    /** 学生实体 */
    private Student student;

    @Override
    public int compareTo(Object o) {
        //按照学生学号升序排序
        if(o instanceof Record){
            Record record = (Record) o;
            return this.student.getStuNum().compareTo(record.student.getStuNum());
        }
        throw new RuntimeException("参数类型不一致！");
    }
}
