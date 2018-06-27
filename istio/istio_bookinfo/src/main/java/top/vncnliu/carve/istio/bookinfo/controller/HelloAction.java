package top.vncnliu.carve.istio.bookinfo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: liuyq
 * Date: 2018/6/21
 * Description:
 */
@RestController(value = "hello")
public class HelloAction {

    @GetMapping
    public String hello(){
        return "book info hello";
    }
}
