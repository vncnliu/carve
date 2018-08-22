package top.vcnliu.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * User: liuyq
 * Date: 2018/7/5
 * Description:
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class TestJpa {

    @Autowired
    private IDBService idbService;

    public static void main(String[] args) {
        SpringApplication.run(TestJpa.class);
    }

    @GetMapping("/hello")
    public String hello(){
        Order order = new Order();
        order.setMemo("test");
        order.setNums(11);
        idbService.saveOrder(order);
        return "success";
    }
}
