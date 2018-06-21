package me.vncnliu.microservices.web.event;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * add description<br>
 * created on 2018/2/13<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class BuyEventListener {

    private static Logger logger = LoggerFactory.getLogger(BuyEventListener.class);

    private DataSource dataSource;

    public BuyEventListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void execute(BuyEvent buyEvent) {
        long begin = System.currentTimeMillis();
        int orderId = 0;
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            String querySql = "SELECT nums FROM goods WHERE id=1 FOR UPDATE ";
            try (PreparedStatement st = connection.prepareStatement(querySql);
                 ResultSet rs = st.executeQuery()) {
                int nums = 0;
                while (rs.next()) {
                    nums = rs.getInt("nums");
                }
                if (nums > 0) {
                    String updateSql = "UPDATE goods SET nums = ? WHERE id=1";
                    String createSql = "insert into orders (goods_id,uid) values (?,?)";
                    try (PreparedStatement updateSt = connection.prepareStatement(updateSql);
                         PreparedStatement createSt = connection.prepareStatement(createSql,Statement.RETURN_GENERATED_KEYS)){
                        updateSt.setInt(1,--nums);
                        createSt.setInt(1,nums);
                        createSt.setInt(2,buyEvent.getUid());
                        updateSt.execute();
                        createSt.executeUpdate();
                        try (ResultSet createRs = createSt.getGeneratedKeys()){
                            if (createRs.next()) {
                                orderId = createRs.getInt(1);
                            }
                        }

                    }
                }
            }
            connection.commit();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        buyEvent.getResult().setResult(orderId+"");
        System.out.println("time:"+(System.currentTimeMillis()-begin));
    }
}
