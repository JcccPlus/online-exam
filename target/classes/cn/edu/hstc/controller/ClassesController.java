package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.ClassesService;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.MajorService;
import cn.edu.hstc.service.StudentService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/class")
public class ClassesController extends BaseController {
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private ClassesService classesService;
    @Autowired
    private StudentService studentService;

    @RequestMapping("/list.html")
    public String list(Classes classes, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        getRequest().setAttribute("orderBy", "id");
        if (!ObjectUtils.isEmpty(searchValue)) {
            classes.setSearchValue(searchValue);
        }
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Classes> classList = classesService.selectClassesList(classes);
        model.addAttribute("classList", classList);
        model.addAttribute("pageInfo", new PageInfo<Classes>(classList));
        model.addAttribute("searchValue", searchValue);
        return "admin/amain3";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/class/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(Classes classes) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(classes.getName())) {
            return error("班级名称不为空且不能与已有班级名称重复");
        }
        if (ObjectUtils.isEmpty(classes.getMajorId()) || classes.getMajorId() == 0) {
            return error("请选择专业");
        }
        if (classesService.insertClass(classes)) {
            return success("添加班级成功");
        } else {
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Classes classes) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(classes.getName())) {
            return error("班级名称不为空且不能与已有班级名称重复");
        }
        if (ObjectUtils.isEmpty(classes.getMajorId())) {
            return error("请选择专业");
        }
        if (classesService.updateClasses(classes)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("classId") Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        Student param = new Student();
        param.setClassId(id);
        PageHelper.startPage(1, 1);
        List<Student> students = studentService.selectStudentList(param);
        if (!students.isEmpty()) {
            return error("该班级关联数据较多，不建议删除！");
        }
        if (classesService.deleteClass(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/getClassesByCid")
    @ResponseBody
    public AjaxResult getClassesByCid(Integer collegeId) {
        Object user = getSession().getAttribute("user");
        if (user instanceof Student) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(collegeId)) {
            return error();
        }
        List<Classes> classes = classesService.selectClassesByCollegeId(collegeId);
        return AjaxResult.success("操作成功", classes);
    }

    @PostMapping("/getClassesByMid")
    @ResponseBody
    public AjaxResult getClassesByMid(Integer majorId, Classes classes) {
        Object user = getSession().getAttribute("user");
        if (user instanceof Student) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(majorId)) {
            return error();
        }
        classes.setMajorId(majorId);
        List<Classes> classesList = classesService.selectClassesList(classes);
        return AjaxResult.success("操作成功", classesList);
    }
}
