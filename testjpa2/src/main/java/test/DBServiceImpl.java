package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * User: liuyq
 * Date: 2018/7/5
 * Description:
 */
@Service
public class DBServiceImpl implements IDBService {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DBServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveOrder(Order order){
        jdbcTemplate.update("insert into test_order (memo,nums) values (?,?)",order.getMemo(),order.getNums());
    }
    @Override
    public void saveReserve(Reserve reserve){
        jdbcTemplate.update("insert into reserve (memo,nums) values (?,?)",reserve.getMemo(),reserve.getNums());
    }

}
