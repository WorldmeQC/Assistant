package org.worldme.assistant.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @Author WorldmeQC
 * @Time 2023/4/4 22:55
 **/
public class ResultUtil {
    public static String handleException(Exception exception) {
        if (exception.getMessage() != null)
            return exception.getMessage();
        return exception.getCause().getMessage();
    }

    public static JsonResult formatResult(Boolean success, String message, Map<String,Object> data) {
        return new JsonResult(success, message, data);
    }

    // 获取当前系统时间的字符串形式 默认格式是：yyyy-MM-dd HH:mm:ss
    public static String getNow(String partten) {
        if (partten != null && !"".equals(partten)) {
            return new SimpleDateFormat(partten).format(new Date());
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    // 格式化指定时间 默认格式是：yyyy-MM-dd HH:mm:ss
    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    // 格式化指定时间 默认格式是：yyyy-MM-dd HH:mm:ss
    public static String formatDate(Date date, String partten) {
        if (partten != null && !"".equals(partten)) {
            return new SimpleDateFormat(partten).format(date);
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

}
