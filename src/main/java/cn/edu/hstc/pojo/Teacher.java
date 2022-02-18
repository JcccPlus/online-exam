package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师实体类tb_teacher
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Teacher {
    /** 工号 */
    private Integer id;
    /** 姓名 */
    private String name;
    /** 密码 */
    private String password;
    /** 性别 */
    private String gender;
    /** 手机 */
    private String phone;
    /** 头像 */
    private String head_pic;
    /** 唯一码 */
    private String code;
    /** 所属学院 */
    private Integer collegeId;
    /** 学院实体 */
    private College college;
}
