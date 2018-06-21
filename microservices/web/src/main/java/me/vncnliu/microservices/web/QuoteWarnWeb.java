package me.vncnliu.microservices.web;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.google.common.eventbus.EventBus;
import me.vncnliu.microservices.web.event.BuyEvent;
import me.vncnliu.microservices.web.event.BuyEventListener;
import me.vncnliu.microservices.web.event.QuoteEvent;
import me.vncnliu.microservices.web.event.QuoteEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.core.publisher.Flux;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 修改测试<br>
 * created on 2018/2/12<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class QuoteWarnWeb {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuoteWarnWeb.class);

    private static EventBus eventBus = new EventBus();

    private static DataSource dataSource;

    @Autowired
    private KafkaTemplate<String, String> template;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Secured("ROLE_ADMIN")
    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/send")
    public String send(@RequestParam("topic") String topic,@RequestParam("msg") String msg) {
        template.send(topic,msg);
        return "success";
    }

    @RequestMapping("/redisIncrement")
    public String redisIncrement() {
        ValueOperations<String, String> operations=redisTemplate.opsForValue();
        return operations.increment("nums",-1L)+"";
    }

    @RequestMapping("/redisSet")
    public String redisSet() {
        ValueOperations<String, String> operations=redisTemplate.opsForValue();
        operations.set("nums", "1000");
        return operations.get("nums")+"";
    }

    @RequestMapping("/redisGet")
    public String redisGet() {
        ValueOperations<String, String> operations=redisTemplate.opsForValue();
        return operations.get("nums")+"";
    }

    @RequestMapping("/buy")
    public String buy(@RequestParam("uid") int uid) {
        Connection connection = null;
        PreparedStatement st = null;
        PreparedStatement updateSt = null;
        PreparedStatement createSt = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String querySql = "select nums from goods where id=1 FOR UPDATE ";
            st = connection.prepareStatement(querySql);
            rs = st.executeQuery();
            int nums=0;
            while(rs.next()) {
                nums = rs.getInt("nums");
            }
            if(nums>0){
                String updateSql = "update goods set nums = " + (--nums) + " where id=1";
                updateSt = connection.prepareStatement(updateSql);
                updateSt.execute();
                String createSql = "insert into orders (goods_id,uid) values ("+nums+","+uid+")";
                createSt = connection.prepareStatement(createSql);
                createSt.execute();

            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(st!=null){
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(updateSt!=null){
                try {
                    updateSt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(createSt!=null){
                try {
                    createSt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }

    @RequestMapping("/addNums")
    public String addNums() {
        Connection connection = null;
        PreparedStatement st = null;
        PreparedStatement updateSt = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            String querySql = "select nums from goods where id=1 FOR UPDATE ";
            st = connection.prepareStatement(querySql);
            rs = st.executeQuery();
            int nums=0;
            while(rs.next()) {
                nums = rs.getInt("nums");
            }
            String updateSql = "update goods set nums = " + (++nums) + " where id=1";
            updateSt = connection.prepareStatement(updateSql);
            updateSt.execute();
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection!=null){
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(st!=null){
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(updateSt!=null){
                try {
                    updateSt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }

    @GetMapping("/event/quotes")
    public Flux<String> getEventQuotes() {
        return Flux.create(emitter -> eventBus.register(new QuoteEventListener(emitter)));
    }

    @RequestMapping("/buyFlux")
    public DeferredResult<String> buyFlux(@RequestParam("uid") int uid) {
        DeferredResult<String> defResult = new DeferredResult<>();
        eventBus.post(new BuyEvent(uid,defResult));
        return defResult;
    }

    @KafkaListener(topics ="quote_2")
    public void processMessage(String content){
        try {
            eventBus.post(QuoteEvent.buildByJson(content));
        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
        }
    }

    @KafkaListener(topics ="order")
    public void processOrderMessage(String content){
        System.err.println(content);
    }

    public static void main(String[] args) {
        try {
            Properties dbConfig = new Properties();
            dbConfig.load(QuoteWarnWeb.class.getClassLoader().getResourceAsStream("db.properties"));
            dataSource = DruidDataSourceFactory.createDataSource(dbConfig);
        } catch (Exception e){
            LOGGER.error(e.getMessage(),e);
        }
        eventBus.register(new BuyEventListener(dataSource));
        SpringApplication.run(QuoteWarnWeb.class, args);
    }
}
