package top.vncnliu.microservices.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: liuyq
 * Date: 2018/4/17
 * Description:
 */
@Controller
public class Test {

    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "test";
    }

    @GetMapping("/order")
    @ResponseBody
    public String order(){
        return "order";
    }
}
