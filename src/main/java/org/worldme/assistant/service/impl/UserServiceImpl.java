package org.worldme.assistant.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.worldme.assistant.mapper.UserMapper;
import org.worldme.assistant.service.IUserService;
import org.worldme.assistant.util.JsonResult;
import org.worldme.assistant.util.JsonTools;
import org.worldme.assistant.util.RedisTool;
import org.worldme.assistant.util.ResultUtil;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

import java.util.Random;
import java.util.UUID;

/**
 * @Author WorldmeQC
 * @Time 2023/4/4 22:02
 **/
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public JsonResult register(Map<String, Object> parameters) {
        Map data = null;
        String message = "注册成功！";//提示信息
        boolean success = false;
        try {
            if (userMapper.getData(parameters) != null)
                throw new Exception("用户名已存在");
            //密码加密处理   MD5算法 加密三次
            //串 + password + 串  交给MD5算法进行加密   串通常称为盐值  盐值也为一个随机的字符串
            String oldPassword = (String) parameters.get("user_password");
            //随机生成盐值
            String salt = UUID.randomUUID().toString().toUpperCase();
            //将密码和盐值结合为一个整体进行加密处理,忽略密码强度，提高安全性
            String md5Password = getMD5Password(oldPassword, salt);
            //将加密后密码重新补全到参数中
            parameters.put("user_password", md5Password);
            //将盐值储存下来
            parameters.put("user_salt", salt);
            //补全日志信息
            parameters.put("state", "T");
            parameters.put("created_time", ResultUtil.getNow(null));
            parameters.put("created_user", parameters.get("user_name"));
            parameters.put("updated_time", ResultUtil.getNow(null));
            parameters.put("updated_user", parameters.get("user_name"));
            if (userMapper.addData(parameters) == 0)
                throw new Exception("未知的数据库异常");
            Map<String, Object> info = new HashMap<>();
            info.put("user_id", parameters.get("user_id"));
            info.put("user_level", 1);
            info.put("user_xp", 0);
            info.put("user_coin", 0L);
            //补全日志信息
            info.put("state", "T");
            info.put("created_time", ResultUtil.getNow(null));
            info.put("created_user", parameters.get("user_name"));
            info.put("updated_time", ResultUtil.getNow(null));
            info.put("updated_user", parameters.get("user_name"));
            if (userMapper.addDetail(info) == 0)
                throw new Exception("未知的数据库异常");
            success = true;
            data = userMapper.getData(parameters);
            data.keySet().removeIf(key -> key == "user_password");
            data.keySet().removeIf(key -> key == "user_salt");
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
        }
        return ResultUtil.formatResult(success, message, data);
    }

    @Override
    public JsonResult login(Map<String, Object> parameters) throws Exception {
        Map data = null;
        String message = "登录成功！";//提示信息
        boolean success = false;
        try {
            Map<String,Object> user =  userMapper.getData(parameters);
            if (user == null)
                throw new Exception("用户名不存在！");
            if (user.get("state").equals("F"))
                throw new Exception("用户已注销！");
            parameters.put("user_password",getMD5Password((String) parameters.get("user_password"), (String) user.get("user_salt")));
            if (!parameters.get("user_password").equals(user.get("user_password")))
                throw new Exception("用户名或密码不正确！");
            data = userMapper.getDetail(user);
            // 生成一个token
            String token = "ey"+UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
            // 将token作为key，将用户信息转换为json放入redis
            Jedis jedis = RedisTool.getResource();
            jedis.set(token, JsonTools.object2json(data));
            // 给缓存中的数据设置一个存活的时间  (这个时间一般都是一个范围,防止缓存雪崩)
            jedis.expire(token,60 * 30 + (new Random().nextInt(60 * 10)));
            data.put("token",token);
            success = true;
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
        }
        return ResultUtil.formatResult(success, message, data);
    }

    /*  定义一个MD5加密算法处理  */
    private String getMD5Password(String password, String salt) {
        //MD5加密算法的调用三次
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();

        }
        return password;
    }
}
