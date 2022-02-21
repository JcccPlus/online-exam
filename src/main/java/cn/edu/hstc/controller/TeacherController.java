package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Major;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.TeacherService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Teacher teacher) {
        if (teacherService.updateTeacher(teacher)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("teacherId") Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            return error("数据异常");
        }
        if (teacherService.deleteTeacher(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }
}
