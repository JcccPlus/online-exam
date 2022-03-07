package cn.edu.hstc.service.impl;

import cn.edu.hstc.dao.*;
import cn.edu.hstc.framework.AjaxResult;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.PaperService;
import cn.edu.hstc.util.ProjectUtil;
import cn.edu.hstc.vo.AutomaticPaperVo;
import cn.edu.hstc.vo.ManualPaperVo;
import cn.edu.hstc.vo.TopicVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.ServletOutputStream;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaperServiceImpl implements PaperService {
    @Autowired
    private PaperDao paperDao;
    @Autowired
    private TopicDao topicDao;
    @Autowired
    private TopicOfPaperDao topicOfPaperDao;

    @Override
    public List<Paper> selectPaperList(Paper paper) {
        return paperDao.selectPaperList(paper);
    }

    @Override
    public Paper selectPaperById(Integer id) {
        return paperDao.selectPaperById(id);
    }

    @Override
    public boolean insertPaper(Paper paper) {
        paper.setCode(ProjectUtil.getUuid().substring(0, 16));
        paper.setCreateTime(DateUtils.getNowDate());
        return paperDao.insertPaper(paper) > 0;
    }

    @Override
    public boolean updatePaper(Paper paper) {
        paper.setUpdateTime(DateUtils.getNowDate());
        return paperDao.updatePaper(paper) > 0;
    }

    @Override
    public boolean deletePaper(Paper paper) {
        return paperDao.deletePaper(paper) > 0;
    }

    @Override
    @Transactional
    public AjaxResult autoGeneratePaper(List<AutomaticPaperVo> vos) {
        Teacher user = (Teacher) ServletUtils.getSession().getAttribute("user");
        List<Topic> topicList = new ArrayList<Topic>();  //记录试卷题目
        double totalScore = 0;   //记录试卷总分
        for (int i = 0; i < vos.size(); i++) {
            AutomaticPaperVo vo = vos.get(i);
            Integer number = vo.getNumber();
            Double score = vo.getScore();
            TopicVo topic = new TopicVo();
            topic.setTeaId(user.getId());
            topic.setStageId(vo.getStage().getId());
            topic.setTypeId(vo.getType().getId());
            topic.setLevelId(vo.getLevel().getId());
            List<Topic> topics = topicDao.selectTopicListByPo(topic);
            if (topics.size() < number) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return AjaxResult.error("《" + vo.getCourse().getName() + "》的”" + vo.getStage().getName() + "“阶段下符合难度为”" + vo.getLevel().getName() + "“的" + vo.getType().getName() + "题目数量不足！");
            }
            randomGetTopics(score, number, topics, topicList);
            totalScore += score * number;
        }
        Collections.sort(topicList);  //对题目按照规定进行排序
        String code = ProjectUtil.getUuid();
        Paper newPaper = new Paper();
        newPaper.setTeaId(user.getId());
        newPaper.setName(vos.get(0).getName());
        newPaper.setCourseId(vos.get(0).getCourse().getId());
        newPaper.setCreateTime(DateUtils.getNowDate());
        newPaper.setScore(totalScore);
        newPaper.setCode(code);
        if (paperDao.insertPaper(newPaper) > 0) {
            Paper param = new Paper();
            param.setCode(code);
            Paper currentPaper = paperDao.selectPaperList(param).get(0);
            for (int i = 0; i < topicList.size(); i++) {
                Topic topic = topicList.get(i);
                TopicOfPaper topicOfPaper = new TopicOfPaper();
                topicOfPaper.setPaperId(currentPaper.getId());
                topicOfPaper.setTopicId(topic.getId());
                topicOfPaper.setSxh(i + 1);
                topicOfPaper.setScore(topic.getScore());
                int row = topicOfPaperDao.insertTopicOfPaper(topicOfPaper);
                if (row == 0) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return AjaxResult.error("程序异常，试卷创建失败！");
                }
            }
            return AjaxResult.success("试卷创建成功！");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return AjaxResult.error("试卷创建失败！");
        }
    }

    /**
     * 下载试卷文档
     */
    @Override
    public String downPaper(Paper paper) {
        //获取该试卷所有题目
        TopicOfPaper topicOfPaper = new TopicOfPaper();
        topicOfPaper.setPaperId(paper.getId());
        List<TopicOfPaper> topics = topicOfPaperDao.selectTopicOfPaperList(topicOfPaper);
        //将题目按题型分配
        List<TopicOfPaper> singleChoiceTopics = new ArrayList<>();  //记录单选题
        List<TopicOfPaper> moreChoiceTopics = new ArrayList<>();  //记录多选题
        List<TopicOfPaper> estimateTopics = new ArrayList<>();   //记录判断题
        List<TopicOfPaper> fillEmptyTopics = new ArrayList<>();   //记录填空题
        List<TopicOfPaper> subjectiveTopics = new ArrayList<>();  //记录主观题
        for (TopicOfPaper topic : topics) {
            switch (topic.getTopic().getType().getName()) {
                case "单选题":
                    singleChoiceTopics.add(topic);
                    break;
                case "多选题":
                    moreChoiceTopics.add(topic);
                    break;
                case "判断题":
                    estimateTopics.add(topic);
                    break;
                case "填空题":
                    fillEmptyTopics.add(topic);
                    break;
                case "主观题":
                    subjectiveTopics.add(topic);
                    break;
            }
        }
        String des_path = ServletUtils.getRequest().getServletContext().getRealPath("/paper");  //试卷文件缓存地址
        File uploadDir = new File(des_path);
        if (!uploadDir.exists()) {
            //没有就创建
            uploadDir.mkdir();
        }
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddhhmmss");
        String now = sf.format(new Date());
        String file_name = paper.getName() + "(" + now + ").docx";  //文件名
        String file_path = des_path + File.separator + file_name;   //文件绝对地址
        System.out.println(file_path);
        try {
            Writer writer = new FileWriter(file_path, true);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write("\t\t\t\t\t\t\t" + paper.getName() + "\n");
            bw.write("\t\t所属课程：" + paper.getCourse().getName() + "\t\t\t\t试卷总分：" + paper.getScore() + "\n");
            if (!singleChoiceTopics.isEmpty()) {
                bw.write("单选题\n");
                for (TopicOfPaper topic : singleChoiceTopics) {
                    bw.write(topic.getSxh() + "." + topic.getTopic().getTitle() + "(" + topic.getScore() + "分)\n");
                    bw.write(topic.getTopic().getOpts() + "\n");
                    bw.write("参考答案：" + topic.getTopic().getAnswer() + "\n题目解析：" + topic.getTopic().getAnalysis() + "\n\n");
                }
            }
            if (!moreChoiceTopics.isEmpty()) {
                bw.write("多选题\n");
                for (TopicOfPaper topic : moreChoiceTopics) {
                    bw.write(topic.getSxh() + "." + topic.getTopic().getTitle() + "(" + topic.getScore() + "分)\n");
                    bw.write(topic.getTopic().getOpts() + "\n");
                    bw.write("参考答案：" + topic.getTopic().getAnswer() + "\n题目解析：" + topic.getTopic().getAnalysis() + "\n\n");
                }
            }
            if (!estimateTopics.isEmpty()) {
                bw.write("判断题\n");
                for (TopicOfPaper topic : estimateTopics) {
                    bw.write(topic.getSxh() + "." + topic.getTopic().getTitle() + "(" + topic.getScore() + "分)\n");
                    bw.write("参考答案：" + topic.getTopic().getAnswer() + "\n题目解析：" + topic.getTopic().getAnalysis() + "\n\n");
                }
            }
            if (!fillEmptyTopics.isEmpty()) {
                bw.write("填空题\n");
                for (TopicOfPaper topic : fillEmptyTopics) {
                    bw.write(topic.getSxh() + "." + topic.getTopic().getTitle() + "(" + topic.getScore() + "分)\n");
                    bw.write("参考答案：" + topic.getTopic().getAnswer() + "\n题目解析：" + topic.getTopic().getAnalysis() + "\n\n");
                }
            }
            if (!subjectiveTopics.isEmpty()) {
                bw.write("主观题\n");
                for (TopicOfPaper topic : subjectiveTopics) {
                    bw.write(topic.getSxh() + "." + topic.getTopic().getTitle() + "(" + topic.getScore() + "分)\n");
                    bw.write("参考答案：" + topic.getTopic().getAnswer() + "\n题目解析：" + topic.getTopic().getAnalysis() + "\n\n");
                }
            }
            bw.flush();
            bw.close();
            writer.close();

            File docx_file = new File(file_path);
            FileInputStream fis = new FileInputStream(docx_file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            String download_filename = file_name.substring(0, file_name.indexOf("(")) + ".docx";
            download_filename = new String(download_filename.getBytes("gb2312"), "ISO8859-1");
            ServletUtils.getResponse().setHeader("Content-Disposition", "attachment; filename=" + download_filename + "");
            //在浏览器中得到文件
            ServletOutputStream out = ServletUtils.getResponse().getOutputStream();
            out.write(b);
            out.flush();
            out.close();
            if (fis != null) {
                fis.close();
            }
            docx_file.delete();  //删除试卷缓存文件
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/paper/paperInfo.html/" + paper.getCode();
    }

    @Override
    public AjaxResult manuallyGeneratePaper(List<ManualPaperVo> vos) {
        return AjaxResult.success();
    }

    /**
     * 随机获取符合条件的题目
     *
     * @param score        该题分数
     * @param num          题目数量
     * @param list         符合要求的题库题目集合
     * @param topicOfPaper 最终得到的试卷中的题目集合
     */
    public static void randomGetTopics(double score, int num, List<Topic> list, List<Topic> topicOfPaper) {
        Set<Integer> index = new HashSet<>();
        Random random = new Random();
        while (index.size() != num) {
            int randomIndex = random.nextInt(list.size());
            index.add(randomIndex);
        }
        for (int i : index) {
            Topic topic = list.get(i);
            topic.setScore(score);
            topicOfPaper.add(topic);
        }
    }

}
