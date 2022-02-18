package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 题目类型实体类tb_type
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Type {
    /** 主键 */
    private Integer id;
    /** 题型 */
    private String name;
}
