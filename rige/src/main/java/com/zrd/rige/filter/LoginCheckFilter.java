package com.zrd.rige.filter;

import com.alibaba.fastjson.JSON;
import com.zrd.rige.common.BaseContext;
import com.zrd.rige.common.R;
import com.zrd.rige.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request1 = (HttpServletRequest) request;
        HttpServletResponse response1 = (HttpServletResponse) response;

        // 1.获取本次请求的URI
        String requestURI = request1.getRequestURI();

        log.info("拦截到请求：{}",requestURI);

        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**", // 上传图片测试
                "/user/sendMsg", // 移动端发送短信
                "/user/login", //移动端登录
        };

        // 2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3.若不需要处理，则直接放行
        if(check){
            log.info("本次请求不需要处理：{}",requestURI);
            chain.doFilter(request1,response1);
            return;
        }

        // 4-1.判断登录状态，若已登录，则直接放行
        if(request1.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request1.getSession().getAttribute("employee"));

            Long empId = (Long) request1.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            // ==========测试 查看线程id ==========
//            long id = Thread.currentThread().getId();
//            log.info("线程id为：{}",id);
            // =============================

            chain.doFilter(request1,response1);
            return;
        }

        // 4-2.判断移动端登录状态，若已登录，则直接放行
        if(request1.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request1.getSession().getAttribute("user"));

            Long userId = (Long) request1.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            chain.doFilter(request1,response1);
            return;
        }

        log.info("用户未登录");
        // 5.若未登录则返回未登录结果，通过输出流方式向客户端页面相应数据
        response1.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI){
        // 快捷方式 -- urls.for 回车
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
