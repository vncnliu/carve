import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

/**
 * add description<br>
 * created on 2018/2/27<br>
 *
 * @author vncnliu
 * @since default 1.0.0
 */
public class MysqlTest {

    @Test
    void main() throws Exception {
        CountDownLatch main = new CountDownLatch(1);
        CountDownLatch child = new CountDownLatch(10);

        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(new AddNumsThread(main,child));
            thread.start();
        }
        main.countDown();
        child.await();
    }

    class AddNumsThread implements Runnable {

        CountDownLatch main;
        CountDownLatch child;

        public AddNumsThread(CountDownLatch main, CountDownLatch child) {
            this.main = main;
            this.child = child;
        }

        @Override
        public void run() {
            try {
                main.await();
                Properties dbConfig = new Properties();
                dbConfig.load(MysqlTest.class.getClassLoader().getResourceAsStream("db.properties"));
                DataSource dataSource = DruidDataSourceFactory.createDataSource(dbConfig);
                Connection connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                String querySql = "select nums from goods where id=1 FOR UPDATE ";
                PreparedStatement st = connection.prepareStatement(querySql);
                ResultSet rs = st.executeQuery();
                int nums=0;
                while(rs.next()) {
                    nums = rs.getInt("nums");
                }
                String updateSql = "update goods set nums = " + (++nums) + " where id=1";
                PreparedStatement updateSt = connection.prepareStatement(updateSql);
                updateSt.execute();
                connection.commit();
                child.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
