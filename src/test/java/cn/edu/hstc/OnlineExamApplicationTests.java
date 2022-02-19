package cn.edu.hstc;

import cn.edu.hstc.dao.AdminDao;
import cn.edu.hstc.dao.CollegeDao;
import cn.edu.hstc.pojo.Admin;
import cn.edu.hstc.pojo.College;
import cn.edu.hstc.service.AdminService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
    public void test03(){
        Admin a = adminService.getAdmin("admin","admin123");
        System.out.println(a);
    }

    @Autowired
    CollegeDao collegeDao;
    @Test
    public void test04(){
        List<College> colleges = collegeDao.selectCollegeList(new College(1, "计算机"));
        System.out.println(colleges);
    }

}
