package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 基类
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity {
    /**
     * 搜索值
     */
    private String searchValue;
    /**
     * 唯一码
     */
    private String code;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 备注
     */
    private String remark;
}
