package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Admin;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Major;
import cn.edu.hstc.service.CollegeService;
import cn.edu.hstc.service.MajorService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/college")
public class CollegeController extends BaseController {
    @Autowired
    CollegeService collegeService;
    @Autowired
    private MajorService majorService;

    @RequestMapping("/list.html")
    public String list(College college, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            model.addAttribute("msg", "无访问权限");
            return "error/404";
        }
        getRequest().setAttribute("orderBy", "id");
        if (!ObjectUtils.isEmpty(searchValue)) {
            college.setSearchValue(searchValue);
        }
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<College> colleges = collegeService.selectCollegeList(college);
        model.addAttribute("colleges", colleges);
        model.addAttribute("pageInfo", new PageInfo<College>(colleges));
        model.addAttribute("searchValue", searchValue);
        return "admin/amain1";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/college/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestParam("add_name") String name) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (StringUtils.isEmpty(name)) {
            return error("学院名称不为空且不能与已有学院名称重复");
        }
        College college = new College();
        college.setName(name);
        try{
            if (collegeService.insertCollege(college)) {
                return success("添加学院成功");
            } else {
                return error("添加失败");
            }
        }catch (Exception e){
            return error("该学院已存在！");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(College college) {
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        if (ObjectUtils.isEmpty(college.getName())) {
            return error("学院名称不为空且不能与已有学院名称重复");
        }
        if (ObjectUtils.isEmpty(college.getId())) {
            return error("数据异常");
        }
        if (collegeService.updateCollege(college)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("collegeId") Integer id) {
        if (ObjectUtils.isEmpty(id)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if (!(user instanceof Admin)) {
            return error("无访问权限");
        }
        Major param = new Major();
        param.setCollegeId(id);
        PageHelper.startPage(1, 1);
        List<Major> majors = majorService.selectMajorList(param);
        if (!majors.isEmpty()) {
            return error("该学院数据关联较多，不建议删除！");
        }
        if (collegeService.deleteCollege(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }
}
