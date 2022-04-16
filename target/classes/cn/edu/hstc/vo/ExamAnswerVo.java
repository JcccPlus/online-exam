package cn.edu.hstc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 考生答案表单类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamAnswerVo {
    /**
     * 题型
     */
    private String type;
    /**
     * 选择题 判断题 主观题答案
     */
    private String answer;
    /**
     * 1-5为填空题各个空的答案
     */
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
}
