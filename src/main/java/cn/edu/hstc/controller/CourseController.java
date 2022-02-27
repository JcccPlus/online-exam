package cn.edu.hstc.controller;

import cn.edu.hstc.pojo.College;
import cn.edu.hstc.pojo.Course;
import cn.edu.hstc.pojo.Teacher;
import cn.edu.hstc.service.CourseService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {
    @Autowired
    private CourseService courseService;

    @RequestMapping("/list.html")
    public String list(Course course, Model model, @ModelAttribute("searchValue") String searchValue, @ModelAttribute("pageNum") String pageNum) {
        getRequest().setAttribute("orderBy", "id");
        course.setTeaId(((Teacher)getSession().getAttribute("user")).getId());
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
}
