package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷实体类tb_paper
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paper extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 试卷名称 */
    private String name;
    /** 试卷总分 */
    private Double score;
    /** 唯一码 */
    private String code;
    /** 所属课程 */
    private String courseId;
    /** 所属教师 */
    private Integer teaId;
    /** 教师实体 */
    private Teacher teacher;
    /** 课程实体 */
    private Course course;
}
