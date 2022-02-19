package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程实体类tb_course
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    /** 主键 */
    private Integer id;
    /** 课程名称 */
    private String name;
    /** 课程简介 */
    private String info;
    /** 所属教师 */
    private Integer teaId;
    /** 教师实体 */
    private Teacher teacher;
}
