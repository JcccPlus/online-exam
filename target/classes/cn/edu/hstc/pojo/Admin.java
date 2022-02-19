package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理员实体类tb_admin
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {
    /** 主键 */
    private Integer id;
    /** 用户名/账号 */
    private String username;
    /** 密码 */
    private String password;
    /** 头像 */
    private String head_pic;
    /** 唯一码 */
    private String code;
}
