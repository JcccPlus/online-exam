package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.CourseService;
import cn.edu.hstc.service.LevelService;
import cn.edu.hstc.service.TopicService;
import cn.edu.hstc.service.TypeService;
import cn.edu.hstc.vo.TopicVo;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/topic")
public class TopicController extends BaseController {
    @Autowired
    private TopicService topicService;
    @Autowired
    private CourseService courseService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private LevelService levelService;

    @RequestMapping("/list.html")
    public String list(TopicVo topicVo, Model model, @ModelAttribute("pageNum") String pageNum) {
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            model.addAttribute("msg","无访问权限");
            return "error/404";
        }
        getRequest().setAttribute("orderBy", "id");
        Teacher teacher = (Teacher) user;
        topicVo.setTeaId(teacher.getId());
        Course param = new Course();
        param.setTeaId(teacher.getId());
        List<Course> currentCourses = courseService.selectCourseList(param);
        model.addAttribute("courseList", currentCourses);
        List<Type> types = typeService.selectTypeList(new Type());
        List<Level> levels = levelService.selectLevelList(new Level());
        model.addAttribute("typeList", types);
        model.addAttribute("levelList", levels);
        if (ObjectUtils.isEmpty(pageNum)) {
            startPage(null);
        } else {
            startPage(Integer.parseInt(pageNum));
        }
        List<Topic> topics = topicService.selectTopicListByPo(topicVo);
        model.addAttribute("topicList", topics);
        model.addAttribute("pageInfo", new PageInfo<Topic>(topics));
        model.addAttribute("searchValue", topicVo);
        return "teacher/tmain2";
    }

    @RequestMapping("/search")
    public String search(TopicVo topicVo, Integer pageNum, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("pageNum", pageNum);
        redirectAttributes.addFlashAttribute("topicVo", topicVo);
        return "redirect:/topic/list.html";
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody List<Topic> topics) {
        return topicService.insertMoreTopic(topics);
    }

    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Topic topic) {
        if (ObjectUtils.isEmpty(topic.getId()) || ObjectUtils.isEmpty(topic.getCode())) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        if (topicService.updateTopic(topic)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("topicId") Integer id, @RequestParam("code") String code) {
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        Topic param = new Topic();
        param.setId(id);
        param.setCode(code);
        if (topicService.deleteTopic(param)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }

    @PostMapping("/getTopic")
    @ResponseBody
    public AjaxResult getTopicByIdAndCode(@RequestParam("topicId") Integer id, @RequestParam("code") String code){
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Object user = getSession().getAttribute("user");
        if(!(user instanceof Teacher)){
            return error("无访问权限");
        }
        Topic param = new Topic();
        param.setId(id);
        param.setCode(code);
        List<Topic> topics = topicService.selectTopicList(param);
        Topic topic = topics.get(0);
        topic.setLevel(null);
        topic.setStage(null);
        topic.setType(null);
        return AjaxResult.success(topics);
    }

}
