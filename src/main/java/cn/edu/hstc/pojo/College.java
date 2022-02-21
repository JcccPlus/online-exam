package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学院实体类tb_college
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class College extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 学院名称 */
    private String name;
}
