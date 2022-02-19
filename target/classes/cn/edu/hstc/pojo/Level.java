package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷难度实体类tb_level
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Level {
    /** 主键 */
    private Integer id;
    /** 难度 */
    private String name;
}
