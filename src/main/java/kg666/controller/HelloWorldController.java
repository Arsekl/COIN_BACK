package kg666.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @RequestMapping("/Hi")
    public String Hello(){
        return "Hello!";
    }
}
