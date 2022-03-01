package cn.edu.hstc.controller;

import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.pojo.Stage;
import cn.edu.hstc.service.StageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/stage")
public class StageController extends BaseController {
    @Autowired
    private StageService stageService;

    @RequestMapping("/list")
    @ResponseBody
    public AjaxResult list(Stage stage){
        if(ObjectUtils.isEmpty(stage.getCourseId()) || ObjectUtils.isEmpty(stage.getCode())){
            return error();
        }
        List<Stage> stages = stageService.selectStageList(stage);
        return AjaxResult.success(stages);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult add(@RequestBody List<Stage> stages){
        return stageService.insertMoreStage(stages);
    }

    @PostMapping("edit")
    @ResponseBody
    public AjaxResult edit(Stage stage){
        if(ObjectUtils.isEmpty(stage.getId()) || ObjectUtils.isEmpty(stage.getCode())){
            return error("数据异常");
        }
        if(stage.getName()!=null && "".equals(stage.getName())){
            return error("阶段名称不能为空");
        }
        if (stageService.updateStage(stage)) {
            return success("更新成功");
        } else {
            return error("更新失败");
        }
    }

    @PostMapping("/delete")
    @ResponseBody
    public AjaxResult delete(@RequestParam("stageId") Integer id, @RequestParam("code") String code){
        if (ObjectUtils.isEmpty(id) || ObjectUtils.isEmpty(code)) {
            return error("数据异常");
        }
        Stage param = new Stage();
        param.setId(id);
        param.setCode(code);
        if (stageService.deleteStage(param)) {
            return success("删除成功");
        } else {
            return error("删除失败");
        }
    }
}
