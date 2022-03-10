package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Major;
import cn.edu.hstc.pojo.Student;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.TeacherService;
import cn.edu.hstc.util.ProjectUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController extends BaseController {
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private CollegeService collegeService;

    @RequestMapping("/list.html")
    public String list(Teacher teacher, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id");
        if (!ObjectUtils.isEmpty(searchValue)) {
            teacher.setSearchValue(searchValue);
        }
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Teacher> teachers = teacherService.selectTeacherList(teacher);
        model.addAttribute("teacherList", teachers);
        model.addAttribute("pageInfo", new PageInfo<Teacher>(teachers));
        model.addAttribute("searchValue", searchValue);
        return "admin/amain4";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/teacher/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(Teacher teacher) {
        if (teacherService.insertTeacher(teacher)) {
            return success("添加教师成功");
        } else {
            return error();
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Teacher teacher) {
        if (teacherService.updateTeacher(teacher)) {
            return success("更新成功");
        } else {
            return error();
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("teacherId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Teacher param = new Teacher();
        param.setId(id);
        param.setCode(code);
        if (teacherService.deleteTeacher(param)) {
            return success("删除成功");
        } else {
            return error();
        }
    }

    @RequestMapping("/self.html")
    public String self(Model model) {
        Teacher currentTeacher = null;
        try{
            currentTeacher = (Teacher) getSession().getAttribute("user");
        }catch (ClassCastException e){
            e.printStackTrace();
            model.addAttribute("无访问权限");
            return "error/404";
        }
        return "teacher/tmain5";
    }

    @PostMapping("/updateSex")
    @ResponseBody
    public AjaxResult updateSex(Integer user_id, String user_code, String user_gender) {
        if (ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)) {
            return error("系统错误，请稍后重试");
        }
        if (ObjectUtils.isEmpty(user_gender)) {
            return error();
        }
        if (!user_gender.equals("男") && !user_gender.equals("女")) {
            return error();
        }
        Teacher teacher = new Teacher();
        teacher.setId(user_id);
        teacher.setCode(user_code);
        teacher.setGender(user_gender);
        if (teacherService.updateTeacher(teacher)) {
            Teacher currentTeacher = teacherService.selectTeacherById(user_id);
            getSession().setAttribute("user", currentTeacher);
            return success("性别修改成功");
        }
        return error();
    }

    @PostMapping("/updatePhone")
    @ResponseBody
    public AjaxResult updatePhone(Integer user_id, String user_code, String user_phone) {
        if (ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)) {
            return error("系统错误，请稍后重试");
        }
        if (ObjectUtils.isEmpty(user_phone)) {
            return error("请输入手机号码");
        } else {
            if (!ProjectUtil.isPhoneNum(user_phone)) {
                return error("手机号码格式错误");
            }
        }
        Teacher teacher = new Teacher();
        teacher.setId(user_id);
        teacher.setCode(user_code);
        teacher.setPhone(user_phone);
        if (teacherService.updateTeacher(teacher)) {
            Teacher currentTeacher = teacherService.selectTeacherById(user_id);
            getSession().setAttribute("user", currentTeacher);
            return success("手机号码更新成功");
        }
        return error();
    }

    @PostMapping("/updatePsw")
    @ResponseBody
    public AjaxResult updatePsw(Integer user_id, String user_code, String oldPsw, String newPsw, String confirmPsw) {
        if (ObjectUtils.isEmpty(user_id) || ObjectUtils.isEmpty(user_code)) {
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
                        Teacher preTeacher = (Teacher) getSession().getAttribute("user");
                        if (!preTeacher.getPassword().equals(oldPsw)) {
                            return error("旧密码输入错误");
                        } else {
                            if (oldPsw.equals(newPsw)) {
                                return error("新密码不能为旧密码");
                            }
                        }
                    }
                }
            }
        }
        Teacher teacher = new Teacher();
        teacher.setId(user_id);
        teacher.setCode(user_code);
        teacher.setPassword(newPsw);
        if (teacherService.updateTeacher(teacher)) {
            Teacher currentTeacher = teacherService.selectTeacherById(user_id);
            getSession().setAttribute("user", currentTeacher);
            return success("密码修改成功");
        }
        return error();
    }

    @PostMapping("/updateHeadPic")
    @ResponseBody
    public AjaxResult updateHeadPic(@RequestParam("headPicFile") MultipartFile file) {
        if (ObjectUtils.isEmpty(file)) {
            return error("数据异常");
        }
        Teacher currentTeacher = null;
        try {
            currentTeacher = (Teacher) getSession().getAttribute("user");
        } catch (ClassCastException e) {
            e.printStackTrace();
            return error("无访问权限");
        }
        return teacherService.updateHeadPic(currentTeacher, file);
    }
}
