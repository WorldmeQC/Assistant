package org.worldme.assistant.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.worldme.assistant.util.ModifyBodyHttpServletRequestWrapper;

@WebFilter(urlPatterns = "/*",filterName = "myFilter")
@Order(1)
public class RequestWrapperFilter implements Filter {

    @Override
    public void init(FilterConfig config) {
        System.out.println("过滤器生效");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {
        ModifyBodyHttpServletRequestWrapper requestWrapper = new ModifyBodyHttpServletRequestWrapper((HttpServletRequest) request);
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
    }
}