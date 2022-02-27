package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.StudentService;
import cn.edu.hstc.util.ProjectUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController extends BaseController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private CollegeService collegeService;

    @RequestMapping("/list.html")
    public String list(Student student, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id");
        if (!ObjectUtils.isEmpty(searchValue)) {
            student.setSearchValue(searchValue);
        }
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Student> students = studentService.selectStudentList(student);
        model.addAttribute("studentList", students);
        model.addAttribute("pageInfo", new PageInfo<Student>(students));
        model.addAttribute("searchValue", searchValue);
        return "admin/amain5";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/student/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(Student student) {
        if (studentService.insertStudent(student)) {
            return success("添加学生成功");
        } else {
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Student student) {
        if (studentService.updateStudent(student)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("studentId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Student param = new Student();
        param.setId(id);
        param.setCode(code);
        if (studentService.deleteStudent(param)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @RequestMapping("/self.html")
    public String self(Student student) {
        return "student/smain3";
    }

    @RequestMapping("/updateSex")
    @ResponseBody
    public AjaxResult updateSex(Integer user_id, String user_code, String user_gender) {
        if(ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)){
            return error("系统错误，请稍后重试");
        }
        if (ObjectUtils.isEmpty(user_gender)) {
            return error();
        }
        if(!user_gender.equals("男") && !user_gender.equals("女")){
            return error();
        }
        Student student = new Student();
        student.setId(user_id);
        student.setCode(user_code);
        student.setGender(user_gender);
        if (studentService.updateStudent(student)) {
            Student currentStudent = studentService.selectStudentById(user_id);
            getSession().setAttribute("user", currentStudent);
            return success("性别修改成功");
        }
        return error("系统错误，修改失败");
    }

    @RequestMapping("/updatePhone")
    @ResponseBody
    public AjaxResult updatePhone(Integer user_id, String user_code, String user_phone) {
        if(ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)){
            return error("系统错误，请稍后重试");
        }
        if (ObjectUtils.isEmpty(user_phone)) {
            return error("请输入手机号码");
        } else {
            if (!ProjectUtil.isPhoneNum(user_phone)) {
                return error("手机号码格式错误");
            }
        }
        Student student = new Student();
        student.setId(user_id);
        student.setCode(user_code);
        student.setPhone(user_phone);
        if (studentService.updateStudent(student)) {
            Student currentStudent = studentService.selectStudentById(user_id);
            getSession().setAttribute("user", currentStudent);
            return success("手机号码更新成功");
        }
        return error("系统错误，更新失败");
    }

    @RequestMapping("/updatePsw")
    @ResponseBody
    public AjaxResult updatePsw(Integer user_id, String user_code, String oldPsw, String newPsw, String confirmPsw) {
        if(ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)){
            return error("系统错误，请稍后重试");
        }
        if (ObjectUtils.isEmpty(oldPsw)) {
            return error("请输入旧密码");
        } else {
            if (ObjectUtils.isEmpty(newPsw)) {
                return error("请输入新密码");
            } else {
                if (!ProjectUtil.isRightPsw(newPsw)) {
                    return error("新密码组成格式不符");
                } else {
                    if (ObjectUtils.isEmpty(confirmPsw)) {
                        return error("请输入确认密码");
                    } else {
                        if (!confirmPsw.equals(newPsw)) {
                            return error("新密码与确认密码不一致");
                        }
                    }
                }
            }
        }
        Student student = new Student();
        student.setId(user_id);
        student.setCode(user_code);
        student.setPassword(newPsw);
        if (studentService.updateStudent(student)) {
            return success("密码修改成功");
        }
        return error("系统错误，修改失败");
    }
}
