package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.ClassesService;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.MajorService;
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
@RequestMapping("/major")
public class MajorController extends BaseController {
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;
    @Autowired
    private ClassesService classesService;

    @RequestMapping("/list.html")
    public String list(Major major, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        getRequest().setAttribute("orderBy", "id");
        if (!ObjectUtils.isEmpty(searchValue)) {
            major.setSearchValue(searchValue);
        }
        List<College> colleges = collegeService.selectCollegeList(new College());
        model.addAttribute("collegeList", colleges);
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Major> majors = majorService.selectMajorList(major);
        model.addAttribute("majorList", majors);
        model.addAttribute("pageInfo", new PageInfo<Major>(majors));
        model.addAttribute("searchValue", searchValue);
        return "admin/amain2";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/major/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(Major major) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(major.getName())) {
            return error("专业名称不为空且不能与已有专业名称重复");
        }
        if (ObjectUtils.isEmpty(major.getCollegeId()) || major.getCollegeId() == 0) {
            return error("请选择学院");
        }
        if (majorService.insertMajor(major)) {
            return success("添加专业成功");
        } else {
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Major major) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(major.getName())) {
            return error("专业名称不为空且不能与已有专业名称重复");
        }
        if (ObjectUtils.isEmpty(major.getCollegeId())) {
            return error("请选择学院");
        }
        if (majorService.updateMajor(major)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("majorId") Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        Classes param = new Classes();
        param.setMajorId(id);
        PageHelper.startPage(1, 1);
        List<Classes> classes = classesService.selectClassesList(param);
        if (!classes.isEmpty()) {
            return error("该专业数据关联较多，不建议删除！");
        }
        if (majorService.deleteMajor(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/getMajorsByCid")
    @ResponseBody
    public AjaxResult getMajorsByCid(Integer collegeId, Major major) {
        Object user = getSession().getAttribute("user");
        if (user instanceof Student) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(collegeId)) {
            return error();
        }
        major.setCollegeId(collegeId);
        List<Major> majors = majorService.selectMajorList(major);
        return AjaxResult.success("操作成功", majors);
    }
}
