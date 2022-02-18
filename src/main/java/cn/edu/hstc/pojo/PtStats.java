package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷题目准确率统计实体类tb_pt_stats
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PtStats {
    /** 主键 */
    private Integer id;
    /** 考试号 */
    private Integer examId;
    /** 试卷题目号 */
    private Integer ptId;
    /** 正确率 */
    private Double accuracy;
    /** 试卷题目实体 */
    private TopicOfPaper topic;
    /** 考试实体 */
    private Exam exam;
}
