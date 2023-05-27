package org.worldme.assistant.service;

import org.worldme.assistant.util.JsonResult;

import java.util.Map;

/**
 * @Author WorldmeQC
 * @Time 2023/4/4 21:55
 **/
public interface IUserService {

    JsonResult register(Map<String, Object> parameters) throws Exception;

    JsonResult login(Map<String, Object> parameters) throws Exception;

    JsonResult update(Map<String, Object> parameters) throws Exception;

    JsonResult getUserInfo(String token) throws Exception;
}
