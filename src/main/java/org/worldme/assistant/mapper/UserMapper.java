package org.worldme.assistant.mapper;

/**
 * @Author WorldmeQC
 * @Time 2023/4/15 10:21
 * 用户模块的持久层
 **/

import java.util.Map;

public interface UserMapper {

    // 插入用户数据
    Integer addData(Map<String, Object> parameters);

    // 插入用户信息
    Integer addDetail(Map<String, Object> parameters);

    // 查询单个用户
    Map<String, Object> getData(Map<String, Object> parameters);

    // 查询单个用户信息
    Map<String, Object> getDetail(Map<String, Object> parameters);

    // 修改用户密码
    Integer updateData(Map<String, Object> parameters);

    // 修改用户信息
    Integer updateDetail(Map<String, Object> parameters);
}
