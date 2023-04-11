package org.worldme.assistant.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.worldme.assistant.util.JsonTools;
import org.worldme.assistant.util.ModifyBodyHttpServletRequestWrapper;
import org.worldme.assistant.util.RedisTool;
import org.worldme.assistant.util.ResultUtil;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * @Author WorldmeQC
 * @Time 2023/4/7 9:20
 **/
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        ModifyBodyHttpServletRequestWrapper requestWrapper = null;
        String token = request.getHeader("token");
        if (token == null || "".equals(token)) {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JsonTools.object2json(ResultUtil.formatResult(false, "用户未登录", null)));
            response.getWriter().flush();
            return false;
        }

        Jedis jedis = RedisTool.getResource();
        if (request instanceof ModifyBodyHttpServletRequestWrapper) {
            requestWrapper = (ModifyBodyHttpServletRequestWrapper) request;
        }
        Map<String,Object> body = JsonTools.json2object(requestWrapper.getBodyJsonStr(), Map.class);
        if (jedis.exists(token)) {
            body.put("profile",jedis.get(token));
            requestWrapper.setBodyJsonStr(JsonTools.object2json(body));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
