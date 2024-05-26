package study.querydsl.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.entity.Hello;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String hello() {
        Hello hello = new Hello("hello");
        return hello.getName();
    }

}
