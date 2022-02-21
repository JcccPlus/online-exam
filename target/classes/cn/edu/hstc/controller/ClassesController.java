package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Classes;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Major;
import cn.edu.hstc.service.ClassesService;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.MajorService;
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

    @RequestMapping("/list.html")
    public String list(Classes classes, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
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
        if (classesService.insertClass(classes)) {
            return success("添加班级成功");
        } else {
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Classes classes) {
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
        if (classesService.deleteClass(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/getClassesByCid")
    @ResponseBody
    public AjaxResult getClassesByCid(Integer collegeId) {
        List<Classes> classes = classesService.selectClassesByCollegeId(collegeId);
        return AjaxResult.success("操作成功", classes);
    }

    @PostMapping("/getClassesByMid")
    @ResponseBody
    public AjaxResult getClassesByMid(Integer majorId, Classes classes) {
        classes.setMajorId(majorId);
        List<Classes> classesList = classesService.selectClassesList(classes);
        return AjaxResult.success("操作成功", classesList);
    }
}
