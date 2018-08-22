package test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: liuyq
 * Date: 2018/7/5
 * Description:
 */
@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class TestJpa2 {

    @Autowired
    private IDBService idbService;

    public static void main(String[] args) {
        SpringApplication.run(TestJpa2.class);
    }

    @GetMapping("/hello")
    public String hello(){
        Reserve reserve = new Reserve();
        reserve.setMemo("test");
        reserve.setNums(11);
        idbService.saveReserve(reserve);
        return "success";
    }
}
