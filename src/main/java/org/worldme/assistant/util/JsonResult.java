package org.worldme.assistant.util;

import lombok.Data;

import java.io.Serializable;

/**
 * Json格式数据响应
 */
@Data
public class JsonResult<E> implements Serializable {
    private Boolean success;
    private String message;
    private E data;

    public JsonResult() {

    }

    public JsonResult(Boolean success) {
        this.success = success;
    }

    public JsonResult(String message) {
        this.message = message;
    }

    public JsonResult(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public JsonResult(Boolean success, String message, E data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

}
