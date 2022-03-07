package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试题实体类tb_topic
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Topic extends BaseEntity implements Comparable {
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
    /** 唯一码 */
    private String code;
    /** 分数 用于记录数据 无对应数据库字段 */
    private Double score;

    @Override
    public int compareTo(Object o) {
        //先按照题型排序，再按照难度排序，最后按照阶段排序
        if(o instanceof Topic){
            Topic topic = (Topic) o;
            if(this.typeId.equals(topic.typeId)) {
                if(this.stageId.equals(topic.stageId)){
                    return Integer.compare(this.levelId, topic.levelId);
                }else{
                    return Integer.compare(this.stageId, topic.stageId);
                }
            }else {
                return Integer.compare(this.typeId,topic.typeId);
            }
        }
        throw new RuntimeException("参数类型不一致！");
    }
}
