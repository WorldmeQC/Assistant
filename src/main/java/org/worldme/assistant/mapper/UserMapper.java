package org.worldme.assistant.mapper;


/* 用户模块的持久层 */

import java.util.Map;

public interface UserMapper {

    // 插入用户数据
    Integer addData(Map<String, Object> parameters);

    // 插入用户信息
    Integer addDetail(Map<String, Object> parameters);

    // 查询单个用户
    Map getData(Map<String, Object> parameters);

    // 查询单个用户信息
    Map getDetail(Map<String, Object> parameters);
}
