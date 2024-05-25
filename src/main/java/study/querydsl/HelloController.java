package study.querydsl;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String hello() {
        Hello hello = new Hello("hello");
        return hello.getName();
    }

}
