package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 班级实体类tb_class
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classes extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 班级名称 */
    private String name;
    /** 所属专业 */
    private Integer majorId;
    /** 专业实体 */
    private Major major;
}
