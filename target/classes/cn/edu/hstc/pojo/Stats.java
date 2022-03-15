package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考试结果统计实体类tb_stats
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    /** 主键 */
    private Integer id;
    /** 所属考试 */
    private Integer examId;
    /** 平均分 */
    private Double sAverage;
    /** 中位数 */
    private Double sMedian;
    /** 最高分 */
    private Double sTop;
    /** 最低分 */
    private Double sLow;
    /** 优秀人数 */
    private Integer good;
    /** 良好人数 */
    private Integer med;
    /** 及格线到良好线的人数 */
    private Integer nor;
    /** 不及格人数 */
    private Integer bad;
    /** 参考人数 */
    private Integer turnout;
    /** 缺考人数 */
    private Integer absence;
    /** 是否需要重新统计标志 */
    private String flag;
    /** 及格线 */
    private Double passScore;
    /** 优秀线 */
    private Double goodScore;
    /** 良好线 */
    private Double medScore;
    /** 考试实体 */
    private Exam exam;
}
