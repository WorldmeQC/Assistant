package org.worldme.assistant.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
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
    public JsonResult getUserInfo(String token) throws Exception {
        Map<String, Object> data = new HashMap<>();
        String message = "获取用户信息成功！";//提示信息
        boolean success = false;
        try {
            Jedis jedis = RedisTool.getResource();
            data = JsonTools.json2object(jedis.get(token), Map.class);
            jedis.close();
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
        }
        return ResultUtil.formatResult(success, message, data);
    }

    @Override
    @Transactional
    public JsonResult register(Map<String, Object> parameters) {
        Map<String, Object> data = new HashMap<>();
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
            info.put("user_nickname", "用户" + info.get("user_id"));
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
            // 处理注册成功后的事项 注册后直接登录 无需再次登录
            // 确定要返回的数据
            String token = getToken(info);
            data.put("token", token);
            success = true;
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultUtil.formatResult(success, message, data);
    }

    @Override
    public JsonResult login(Map<String, Object> parameters) throws Exception {
        Map<String, Object> data = new HashMap<>();
        String message = "登录成功！";//提示信息
        boolean success = false;
        try {
            // 先从数据库根据用户名查询对应用户
            Map<String, Object> user = userMapper.getData(parameters);
            if (user == null)
                throw new Exception("用户名不存在！");
            if (user.get("state").equals("F"))
                throw new Exception("用户已注销！");
            // 验证异常通过后 对比密码
            parameters.put("user_password", getMD5Password((String) parameters.get("user_password"), (String) user.get("user_salt")));
            if (!parameters.get("user_password").equals(user.get("user_password")))
                throw new Exception("用户名或密码不正确！");
            // 密码对比通过
            String token = getToken(user);
            data.put("token", token);
            success = true;
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
        }
        return ResultUtil.formatResult(success, message, data);
    }

    @Override
    public JsonResult update(Map<String, Object> parameters) throws Exception {
        Map data = null;
        String message = "修改成功！";//提示信息
        boolean success = false;
        try {
            Map<String, Object> user = userMapper.getData(parameters);
            if (user == null)
                throw new Exception("用户不存在！");
            if (user.get("state").equals("F"))
                throw new Exception("用户已注销！");
            // 判断是否需要修改密码
            if (parameters.get("need_update_password").equals("T")) {
                parameters.put("user_password", getMD5Password((String) parameters.get("user_password"), (String) user.get("user_salt")));
                if (!parameters.get("user_password").equals(user.get("user_password")))
                    throw new Exception("密码不正确！");
                // 密码验证通过 可以修改密码
                // 根据新密码重新加密
                //随机生成盐值
                String salt = UUID.randomUUID().toString().toUpperCase();
                //将密码和盐值结合为一个整体进行加密处理,忽略密码强度，提高安全性
                String md5Password = getMD5Password((String) parameters.get("new_password"), salt);
                //将加密后密码重新补全到参数中
                parameters.put("user_password", md5Password);
                //将盐值储存下来
                parameters.put("user_salt", salt);
                //补全日志信息
                parameters.put("updated_time", ResultUtil.getNow(null));
                parameters.put("updated_user", user.get("user_name"));
                if (userMapper.updateData(parameters) == 0)
                    throw new Exception("未知的数据库异常");
            } else {
                // 不修改密码 修改个人信息
                //补全日志信息
                parameters.put("updated_time", ResultUtil.getNow(null));
                parameters.put("updated_user", user.get("user_name"));
                if (userMapper.updateDetail(parameters) == 0)
                    throw new Exception("未知的数据库异常");
                data = userMapper.getDetail(parameters);
            }
            Jedis jedis = RedisTool.getResource();
            jedis.del((String) parameters.get("token"));
            jedis.close();
            success = true;
        } catch (Exception e) {
            message = ResultUtil.handleException(e);
        }
        return ResultUtil.formatResult(success, message, data);
    }

    // 定义一个MD5加密算法处理
    private String getMD5Password(String password, String salt) {
        //MD5加密算法的调用三次
        for (int i = 0; i < 3; i++) {
            password = DigestUtils.md5DigestAsHex((salt + password + salt).getBytes()).toUpperCase();

        }
        return password;
    }


    // 生成token 并放入redis中 只允许redis中每个用户最多存在一个token，防止多次登录产生多个token
    private String getToken(Map<String, Object> user) {
        // 从池中取出一个redis连接
        Jedis jedis = RedisTool.getResource();
        // 先通过id寻找之前的token
        String oldToken = jedis.get(String.valueOf(user.get("user_id")));
        if (oldToken != null) {
            // 删除之前的token
            jedis.del(oldToken);
        }
        // 重新生成一个token 利用UUID生成无序字符串并将其中的"-"去掉
        String token = "ey" + UUID.randomUUID().toString().toLowerCase().replaceAll("-", "");
        // 将用户id作为key 覆盖token
        jedis.set(String.valueOf(user.get("user_id")), token);
        // 从数据库中取出数据
        Map<String, Object> data = userMapper.getDetail(user);
        data.put("user_name",user.get("user_name"));
        // 将token作为key，将用户信息转换为json放入redis
        jedis.set(token, JsonTools.object2json(data));
        // 给缓存中的数据设置一个存活的时间  (这个时间一般都是一个范围,防止缓存雪崩) 时间为3天，3分钟内随机波动
        // 取一个随机3分钟内的时间
        int seconds = new Random().nextInt(60 * 3);
        jedis.expire(String.valueOf(user.get("user_id")), 3 * 24 * 60 * 60 + seconds);
        jedis.expire(token, 3 * 24 * 60 * 60 + seconds);
        jedis.close();
        return token;
    }

}
