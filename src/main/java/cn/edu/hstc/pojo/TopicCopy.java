package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试题缓存实体类tb_topic_copy
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicCopy {
    /** 主键 */
    private Integer id;
    /** 题目内容 */
    private String title;
    /** 选项内容 */
    private String opts;
    /** 参考答案 */
    private String answer;
    /** 题目解析 */
    private String analysis;
    /** 题目类型 */
    private Integer typeId;
    /** 题目难度 */
    private Integer levelId;
    /** 题目阶段 */
    private Integer stageId;
    /** 题型实体 */
    private Type type;
    /** 难度实体 */
    private Level level;
    /** 阶段实体 */
    private Stage stage;
}
