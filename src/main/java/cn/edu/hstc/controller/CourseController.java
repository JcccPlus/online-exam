package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.pojo.Paper;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.CourseService;
import cn.edu.hstc.service.PaperService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private PaperService paperService;

    @RequestMapping("/list.html")
    public String list(Course course, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id");
        course.setTeaId(((Teacher) getSession().getAttribute("user")).getId());
        if (!ObjectUtils.isEmpty(searchValue)) {
            course.setSearchValue(searchValue);
        }
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Course> courses = courseService.selectCourseList(course);
        model.addAttribute("courseList", courses);
        model.addAttribute("pageInfo", new PageInfo<Course>(courses));
        model.addAttribute("searchValue", searchValue);
        return "teacher/tmain1";
    }

    @RequestMapping("/search")
    public String search(String searchValue, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("searchValue", searchValue);
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        return "redirect:/course/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody List<Course> courses, HttpSession session) {
        return courseService.insertMoreCourse(courses, session);
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Course course) {
        if (ObjectUtils.isEmpty(course.getId()) || ObjectUtils.isEmpty(course.getCode())) {
            return error("数据异常");
        }
        if (ObjectUtils.isEmpty(course.getName())) {
            return error("课程名称不能为空");
        }
        if (courseService.updateCourse(course)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("courseId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Paper paper = new Paper();
        paper.setCourseId(id);
        if (paperService.selectPaperList(paper).size() > 0) {
            return error("删除失败！该课程下已关联相关试卷！");
        }
        Course param = new Course();
        param.setId(id);
        param.setCode(code);
        if (courseService.deleteCourse(param)) {
            return success("删除成功");
        } else {
            return error();
        }
    }
}
