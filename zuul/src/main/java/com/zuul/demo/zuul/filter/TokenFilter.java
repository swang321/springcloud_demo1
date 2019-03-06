package com.zuul.demo.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author whh
 */
@Slf4j
public class TokenFilter extends ZuulFilter {

    @Override
    public String filterType() {
        // 可以在请求被路由之前调用
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 5;
    }

    @Override
    public boolean shouldFilter() {
        // 是否执行该过滤器，此处为true，说明需要过滤
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        log.info("token filter {}, {}", request.getMethod(), request.getRequestURL().toString());
        String token = request.getParameter("token");

        if (StringUtils.isBlank(token)) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            requestContext.setResponseBody("token is empty");
            requestContext.set("isSuccess", false);
        } else {
            requestContext.setSendZuulResponse(true);
            requestContext.setResponseStatusCode(HttpStatus.SC_OK);
            requestContext.set("isSuccess", true);
        }

        return null;
    }
}
