package org.worldme.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.worldme.assistant.service.IUserService;
import org.worldme.assistant.util.JsonResult;

import java.util.Map;


//@Controller
@RestController  //等于@Controller + @ResponseBody
@RequestMapping("UserService")
public class UserController {
    @Autowired
    private IUserService userService;
    /*
    //@ResponseBody  表示此方法的响应结果以json格式进行数据响应
    @RequestMapping("register")
    public JsonResult<Void> register(User user) {
        //创建响应结果对象
        JsonResult<Void> result = new JsonResult<>();
        try {
            userService.register(user);
            result.setState(200);
            result.setMessage("用户注册成功");
        } catch (UsernameDepulitedException e) {
            result.setState(4000);
            result.setMessage("用户名被占用");
        } catch (InsertException e) {
            result.setState(5000);
            result.setMessage("注册时产生未知异常");
        }
        return result;
    }
    */

    @PostMapping("register")
    public JsonResult register(@RequestBody Map<String, Object> parameters) throws Exception {
        return userService.register(parameters);
    }

    @PostMapping("login")
    public JsonResult login(@RequestBody Map<String, Object> parameters) throws Exception {
        return userService.login(parameters);
    }

}
