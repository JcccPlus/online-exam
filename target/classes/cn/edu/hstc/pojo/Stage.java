package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程阶段实体类tb_stage
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stage extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 阶段名称 */
    private String name;
    /** 阶段简介 */
    private String info;
    /** 所属课程 */
    private Integer courseId;
    /** 课程实体 */
    private Course course;
}
