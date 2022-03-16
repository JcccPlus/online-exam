package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.CourseService;
import cn.edu.hstc.service.TeacherService;
import cn.edu.hstc.util.ProjectUtil;
import com.github.pagehelper.PageHelper;
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
    @Autowired
    private CourseService courseService;

    @RequestMapping("/list.html")
    public String list(Teacher teacher, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
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
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(teacher.getCollegeId()) || teacher.getCollegeId() == 0) {
            return error("请选择学院");
        }
        if (ObjectUtils.isEmpty(teacher.getTeaNum())) {
            return error("工号不为空且不能与已有工号重复");
        }
        if (ObjectUtils.isEmpty(teacher.getName())) {
            return error("姓名不为空");
        }
        if (ObjectUtils.isEmpty(teacher.getGender())) {
            return error("性别不为空");
        } else {
            if (!teacher.getGender().equals("男") && !teacher.getGender().equals("女")) {
                return error("性别数据出错");
            }
        }
        try{
            if (teacherService.insertTeacher(teacher)) {
                return success("添加教师成功");
            } else {
                return error();
            }
        }catch (Exception e){
            return error("该工号已存在！");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Teacher teacher) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(teacher.getCollegeId()) || teacher.getCollegeId() == 0) {
            return error("请选择学院");
        }
        if (ObjectUtils.isEmpty(teacher.getTeaNum())) {
            return error("工号不为空且不能与已有工号重复");
        }
        if (ObjectUtils.isEmpty(teacher.getName())) {
            return error("姓名不为空");
        }
        if (ObjectUtils.isEmpty(teacher.getGender())) {
            return error("性别不为空");
        } else {
            if (!teacher.getGender().equals("男") && !teacher.getGender().equals("女")) {
                return error("性别数据出错");
            }
        }
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
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        Course course = new Course();
        course.setTeaId(id);
        PageHelper.startPage(1, 1);
        List<Course> courses = courseService.selectCourseList(course);
        if(!courses.isEmpty()){
            return error("该教师关联数据较多，不建议删除");
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
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return "error/404";
        }
        return "teacher/tmain5";
    }

    @PostMapping("/updateSex")
    @ResponseBody
    public AjaxResult updateSex(String user_gender) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error();
        }
        if (ObjectUtils.isEmpty(user_gender)) {
            return error();
        }
        if (!user_gender.equals("男") && !user_gender.equals("女")) {
            return error();
        }
        Teacher currentTeacher = (Teacher) user;
        Teacher teacher = new Teacher();
        teacher.setId(currentTeacher.getId());
        teacher.setCode(currentTeacher.getCode());
        teacher.setGender(user_gender);
        if (teacherService.updateTeacher(teacher)) {
            currentTeacher.setGender(user_gender);
            getSession().setAttribute("user", currentTeacher);
            return success("性别修改成功");
        }
        return error();
    }

    @PostMapping("/updatePhone")
    @ResponseBody
    public AjaxResult updatePhone(String user_phone) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error();
        }
        if (ObjectUtils.isEmpty(user_phone)) {
            return error("请输入手机号码");
        } else {
            if (!ProjectUtil.isPhoneNum(user_phone)) {
                return error("手机号码格式错误");
            }
        }
        Teacher currentTeacher = (Teacher) user;
        Teacher teacher = new Teacher();
        teacher.setId(currentTeacher.getId());
        teacher.setCode(currentTeacher.getCode());
        teacher.setPhone(user_phone);
        if (teacherService.updateTeacher(teacher)) {
            currentTeacher.setPhone(user_phone);
            getSession().setAttribute("user", currentTeacher);
            return success("手机号码更新成功");
        }
        return error();
    }

    @PostMapping("/updatePsw")
    @ResponseBody
    public AjaxResult updatePsw(String oldPsw, String newPsw, String confirmPsw, String code) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error();
        }
        Teacher currentTeacher = (Teacher) user;
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
                        if (!currentTeacher.getPassword().equals(oldPsw)) {
                            return error("旧密码输入错误");
                        } else {
                            if (oldPsw.equals(newPsw)) {
                                return error("新密码不能为旧密码");
                            }else{
                                if(!code.equals(getSession().getAttribute("VerifyCode"))){
                                    return error("验证码错误");
                                }
                            }
                        }
                    }
                }
            }
        }
        Teacher teacher = new Teacher();
        teacher.setId(currentTeacher.getId());
        teacher.setCode(currentTeacher.getCode());
        teacher.setPassword(newPsw);
        if (teacherService.updateTeacher(teacher)) {
            currentTeacher.setPassword(newPsw);
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
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        Teacher currentTeacher = (Teacher) user;
        return teacherService.updateHeadPic(currentTeacher, file);
    }
}
