package com.cloudconfigclient.demo.configclient.fillter;


import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * @Author whh
 */
@Slf4j
public class BodyFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        String bodyStr = readBody(request);
        log.info("original body:{}", bodyStr);
        RequestWrapper requestWrapper = new RequestWrapper(request);
        filterChain.doFilter(requestWrapper, res);
    }

    @Override
    public void destroy() {

    }

    /**
     * 获取request请求body中参数
     */
    private static String readBody(HttpServletRequest request) {
        BufferedReader br;
        StringBuilder sb = new StringBuilder();

        try {
            //  getReader 或者    getInputStream  只能调用其中一个且只有一次可以正常获取body中内容
            //  eg: 获取  request 中的 body    参数传递到 controller  需要另外处理
            br = request.getReader();
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            // 关闭 BufferedReader
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
