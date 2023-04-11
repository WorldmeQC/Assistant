package org.worldme.assistant.util;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.dubbo.common.utils.IOUtils;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ModifyBodyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    // 重新赋值的body数据
    private String bodyJsonStr;

    public ModifyBodyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        try {
            //request获取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            bodyJsonStr = IOUtils.read(reader);
        } catch (Exception e) {
        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 必须指定utf-8编码，否则json请求数据中如果包含中文，会出现异常
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bodyJsonStr.getBytes("utf-8"));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }
            @Override
            public boolean isReady() {
                return false;
            }
            @Override
            public void setReadListener(ReadListener readListener) {
            }
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
        return servletInputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    public String getBodyJsonStr() {
        return bodyJsonStr;
    }

    public void setBodyJsonStr(String bodyJsonStr) {
        this.bodyJsonStr = bodyJsonStr;
    }
}