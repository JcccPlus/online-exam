package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Major;
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
@RequestMapping("/major")
public class MajorController extends BaseController {
    @Autowired
    private MajorService majorService;
    @Autowired
    private CollegeService collegeService;

    @RequestMapping("/list.html")
    public String list(Major major, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
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
        if (majorService.insertMajor(major)) {
            return success("添加专业成功");
        } else {
            return error("添加失败");
        }
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Major major) {
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
        if (majorService.deleteMajor(id)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/getMajorsByCid")
    @ResponseBody
    public AjaxResult getMajorsByCid(Integer collegeId, Major major) {
        major.setCollegeId(collegeId);
        List<Major> majors = majorService.selectMajorList(major);
        return AjaxResult.success("操作成功", majors);
    }
}
