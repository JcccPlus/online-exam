package cn.edu.hstc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicVo {
    private Integer teaId;
    private Integer courseId;
    private String courseName;
    private Integer stageId;
    private String stageName;
    private Integer typeId;
    private Integer levelId;
}
