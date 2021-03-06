package com.smart.mixdao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Service;

import com.smart.User;

/**
 * @author 陈雄华
 * @version 1.0
 */
@Service("userService")
public class UserService extends BaseService {

    private HibernateTemplate hibernateTemplate;
    private ScoreService scoreService;

    @Autowired
    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    @Autowired
    public void setScoreService(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    public void logon(String userName) {
        System.out.println("before userService.updateLastLogonTime()..");
        // use Hibernate
        updateLastLogonTime(userName);
        System.out.println("end userService.updateLastLogonTime()..");

        System.out.println("before scoreService.addScore()..");
        // use JDBC
        scoreService.addScore(userName, 20);
        System.out.println("end scoreService.addScore()..");
    }

    public void updateLastLogonTime(String userName) {
        User user = hibernateTemplate.get(User.class, userName);
        user.setLastLogonTime(System.currentTimeMillis());
        hibernateTemplate.update(user);
        /**
         * 重要：
         * 因为还用了 SpringJDBC，所以此处需要强制刷新 Hibernate 的一级缓存！
         * 否则，SpringJDBC 的数据会被 Hibernate 一级缓存的自动提交（延迟策略）覆盖掉！
         */
        hibernateTemplate.flush();
    }

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("com/smart/mixdao/applicationContext.xml");
        UserService service = (UserService) ctx.getBean("userService");
        JdbcTemplate jdbcTemplate = (JdbcTemplate) ctx.getBean("jdbcTemplate");
        jdbcTemplate.execute("DELETE FROM t_user WHERE user_name='tom'");
        //插入一条记录，初始分数为10
        String sql = "INSERT INTO t_user(user_name,password,score,last_logon_time) VALUES('tom','123456',10,"
                + System.currentTimeMillis() + ")";
        jdbcTemplate.execute(sql);

        System.out.println("before userService.logon()..");
        service.logon("tom");
        System.out.println("after userService.logon()..");

        int score = jdbcTemplate.queryForObject("SELECT score FROM t_user WHERE user_name ='tom'", Integer.class);
        System.out.println("score:" + score);
        jdbcTemplate.execute("DELETE FROM t_user WHERE user_name='tom'");
    }
}
