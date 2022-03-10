package cn.edu.hstc;

import cn.edu.hstc.dao.AdminDao;
import cn.edu.hstc.dao.CollegeDao;
import cn.edu.hstc.dao.ExamDao;
import cn.edu.hstc.dao.MajorDao;
import cn.edu.hstc.framework.util.DateUtils;
import cn.edu.hstc.framework.util.ServletUtils;
import cn.edu.hstc.pojo.*;
import cn.edu.hstc.service.AdminService;
import cn.edu.hstc.service.ExamService;
import cn.edu.hstc.util.ProjectUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OnlineExamApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AdminDao adminDao;

    @Autowired
    AdminService adminService;

    @Test
    public void test01() throws SQLException {
        System.out.println(dataSource.getClass());
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    @Test
    public void test02() {
        String sql = "insert into tb_type(name) values('判断题')";
        int row = jdbcTemplate.update(sql);
        if (row > 0) {
            System.out.println("添加成功");
        }
    }

    @Test
    public void test03() {
        Admin a = adminService.getAdmin("admin", "admin123");
        System.out.println(a);
    }

    @Autowired
    CollegeDao collegeDao;

    @Test
    public void test04() {
        List<College> colleges = collegeDao.selectCollegeList(new College(1, "计算机"));
        System.out.println(colleges);
    }

    @Autowired
    MajorDao majorDao;

    @Test
    public void test05() {
        List<Major> majors = majorDao.selectMajorList(new Major());
        System.out.println(majors);
    }

    @Test
    public void test06() {
        System.out.println(ProjectUtil.getMD5String("2018115157").substring(0, 16));
    }

    @Autowired
    private ExamDao examDao;

    @Test
    public void test07() {
        Date date = new Date();
        long startTime = date.getTime();
        Integer time = 60;
        long endTime = time * 60 * 1000;
        Exam param = new Exam();
        /*param.setStart(date);
        param.setTime(time);
        param.setEnd(new Date(startTime + endTime));
        examDao.insertExam(param);*/
        System.out.println(examDao.selectExamList(param));
        System.out.println(new Date(startTime + endTime));
    }

    @Test
    public void test08() {
        String psw = "12345678a\\-[]啊";
        System.out.println(ProjectUtil.isRightPsw(psw));
    }

    @Test
    public void test09() {
        String psw = "1{{separator}}2{{separator}}3{{separator}}";
        String[] s = psw.split("_");
        System.out.println(Arrays.toString(s));
        System.out.println(psw.substring(0, psw.lastIndexOf("{{separator}}")));
        System.out.println((psw.substring(0, psw.lastIndexOf("{{separator}}"))).substring(0, (psw.substring(0, psw.lastIndexOf("{{separator}}"))).lastIndexOf("{{separator}}")));
        System.out.println(psw.substring(0, psw.lastIndexOf("{{separator}}")));
    }

    @Test
    public void test10() {
        List<Topic> topics = new ArrayList<>();
        Topic topic1 = new Topic();
        topic1.setStageId(1);
        topic1.setTypeId(5);
        topic1.setLevelId(2);
        Topic topic2 = new Topic();
        topic2.setStageId(1);
        topic2.setTypeId(5);
        topic2.setLevelId(1);
        Topic topic3 = new Topic();
        topic3.setStageId(1);
        topic3.setTypeId(1);
        topic3.setLevelId(3);
        Topic topic4 = new Topic();
        topic4.setStageId(1);
        topic4.setTypeId(1);
        topic4.setLevelId(3);
        Topic topic5 = new Topic();
        topic5.setStageId(1);
        topic5.setTypeId(3);
        topic5.setLevelId(1);
        topics.add(topic1);
        topics.add(topic2);
        topics.add(topic3);
        topics.add(topic4);
        topics.add(topic5);
        Collections.sort(topics);
        /*topics.sort(new Comparator<Topic>() {
            @Override
            public int compare(Topic o1, Topic o2) {
                if (o1 instanceof Topic && o2 instanceof Topic) {
                    if (o1.getTypeId().equals(o2.getTypeId())) {
                        if(o1.getStageId().equals(o2.getStageId())){
                            return Integer.compare(o1.getLevelId(), o2.getLevelId());
                        }else{
                            return Integer.compare(o1.getStageId(), o2.getStageId());
                        }
                    } else {
                        return Integer.compare(o1.getTypeId(), o2.getTypeId());
                    }
                }
                throw new RuntimeException("参数类型不一致！");
            }
        });*/
        System.out.println(topics);
        System.out.println(ServletUtils.getRequest().getServletContext().getRealPath("/exam"));
        try {
            System.out.println(ResourceUtils.getURL("classpath:/resources").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test11() {
        String regex = "[{{][separator][}}]";
        String str = "11{{separator}}22{{separator}}33{{separator}}";
        String[] split = str.split(regex);
        System.out.println(Arrays.toString(split));
        StringTokenizer t = new StringTokenizer(str, "{{separator}}");
        while (t.hasMoreElements()) {
            System.out.println(t.nextToken());
        }
    }

    @Autowired
    ExamService examService;

    @Test
    public void test12() {
        Exam exam = new Exam();
        Paper paper = new Paper();
        paper.setTeaId(3);
        exam.setPaper(paper);
        exam.setSearchValue("Java程序设计语言阿");
        List<Exam> exams = examService.selectExamList(exam);
        for (Exam exam1 : exams) {
            System.out.println(exam1.toString());
        }
    }

    @Test
    public void test13() {
        Exam exam = new Exam();
        exam.setClassId(3);
        exam.setSearchValue("Java");
        List<Exam> exams = examService.selectCurrentExam(exam,1);
        for (Exam exam1 : exams) {
            System.out.println(exam1.toString());
        }
    }
}
