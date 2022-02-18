package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试卷题目映射关系实体类tb_paper_topic
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicOfPaper {
    /** 主键 */
    private Integer id;
    /** 试卷中的顺序号 */
    private Integer sxh;
    /** 试题号 */
    private Integer topicId;
    /** 试题分数 */
    private Double score;
    /** 所属试卷 */
    private Integer paperId;
    /** 试卷实体 */
    private Paper paper;
    /** 试题实体 */
    private Topic topic;
}
