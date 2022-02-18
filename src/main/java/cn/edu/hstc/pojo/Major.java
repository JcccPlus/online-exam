package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 专业实体类tb_major
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Major {
    /** 主键 */
    private Integer id;
    /** 专业名称 */
    private String name;
    /** 所属学院 */
    private Integer collegeId;
    /** 学院实体 */
    private College college;
}
