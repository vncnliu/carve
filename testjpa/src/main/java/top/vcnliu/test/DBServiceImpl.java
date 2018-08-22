package top.vcnliu.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

/**
 * User: liuyq
 * Date: 2018/7/5
 * Description:
 */
@Service
public class DBServiceImpl implements IDBService {

    private JdbcTemplate jdbcTemplate;

    private RestTemplate restTemplate;

    @Autowired
    public DBServiceImpl(JdbcTemplate jdbcTemplate,RestTemplateBuilder restTemplateBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        restTemplate = restTemplateBuilder.build();
    }

    @Override
    public void saveOrder(Order order){
        jdbcTemplate.update("insert into test_order (memo,nums) values (?,?)",order.getMemo(),order.getNums());
        restTemplate.getForEntity("http://localhost:8642/hello",null);
    }

}
