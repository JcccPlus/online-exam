package cn.edu.hstc.vo;

import cn.edu.hstc.pojo.Topic;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 手动生成试卷的表单数据类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualPaperVo extends Topic {
    @JsonProperty("course")
    private Integer courseId;
    private Double score;
}
