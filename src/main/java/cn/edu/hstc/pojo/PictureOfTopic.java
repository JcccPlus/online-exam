package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 试题图片映射关系实体类tb_picture
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PictureOfTopic {
    /** 主键 */
    private Integer id;
    /** 图片路径 */
    private String path;
    /** 所属试题 */
    private Integer topId;
    /** 试题实体 */
    private Topic topic;
}
