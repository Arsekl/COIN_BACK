package kg666.controller;

import kg666.service.UserService;
import kg666.vo.ResponseVO;
import kg666.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user/register")
    public ResponseVO register(@RequestBody UserVO userVO){
        return userService.register(userVO);
    }

    @PostMapping("/user/login")
    public ResponseVO login(@RequestBody UserVO userVO){
        return userService.login(userVO);
    }
}
