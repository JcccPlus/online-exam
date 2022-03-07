package cn.edu.hstc.vo;

import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.pojo.Level;
import cn.edu.hstc.pojo.Stage;
import cn.edu.hstc.pojo.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 自动生成试卷的表单数据类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutomaticPaperVo {
    private String name;
    private Course course;
    private Stage stage;
    private Type type;
    private Level level;
    private Integer number;
    private Double score;
}
