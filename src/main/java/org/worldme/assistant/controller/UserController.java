package org.worldme.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.worldme.assistant.service.IUserService;
import org.worldme.assistant.util.JsonResult;

import java.util.Map;


//@Controller
@RestController  //等于@Controller + @ResponseBody
@RequestMapping("UserService")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("register")
    public JsonResult register(@RequestBody Map<String, Object> parameters) throws Exception {
        return userService.register(parameters);
    }

    @PostMapping("login")
    public JsonResult login(@RequestBody Map<String, Object> parameters) throws Exception {
        return userService.login(parameters);
    }

    @PostMapping("update")
    public JsonResult update(@RequestBody Map<String, Object> parameters) throws Exception {
        return userService.update(parameters);
    }

    @PostMapping("getInfo")
    public JsonResult getInfo(@RequestHeader("token") String token) throws Exception {
        return userService.getUserInfo(token);
    }
}
