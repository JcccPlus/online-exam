package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 考试实体类tb_exam
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 开考时间 */
    private Date start;
    /** 考试时长 */
    private Integer time;
    /** 结束时间 */
    private Date end;
    /** 总分 */
    private Double score;
    /** 允许迟到时间 */
    private Integer lateTime;
    /** 唯一码 */
    private String code;
    /** 隶属试卷号 */
    private Integer paperId;
    /** 所属班级 */
    private Integer classId;
    /** 试卷实体 */
    private Paper paper;
    /** 班级实体 */
    private Classes classes;
}
