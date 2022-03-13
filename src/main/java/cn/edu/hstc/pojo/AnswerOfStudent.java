package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生试卷题目答案映射关系实体类tb_answer
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerOfStudent extends BaseEntity {
    /** 主键 */
    private Integer id;
    /** 考试记录号 */
    private Integer recordId;
    /** 试卷题目号 */
    private Integer ptId;
    /** 答案 */
    private String answer;
    /** 是否正确 */
    private String isRight;
    /** 该题得分 */
    private Double score;
    /** 考试记录实体 */
    private Record record;
    /** 试卷题目实体 */
    private TopicOfPaper topic;
}
