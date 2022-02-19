package cn.edu.hstc.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生实体类tb_student
 *
 * @author Jc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student extends BaseEntity {
    /** 学号 */
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
    /** 所属班级 */
    private Integer classId;
    /** 班级实体 */
    private Classes classes;
}
