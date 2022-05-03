package com.liner.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.liner.reggie.common.BaseContext;
import com.liner.reggie.common.R;
import com.sun.org.apache.regexp.internal.RE;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** 检查用户是否已经登录 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

  // 路径匹配器,支持通配符
  public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    // 获取本次请求的URI
    String requestURI = request.getRequestURI();

    log.info("拦截到请求{}", request.getRequestURI());

    // 把不需要处理的请求放行
    String[] urls =
        new String[] {
          "/employee/login", "/employee/logout", "/backend/**", "/front/**", "/common/**","/user/sendMsg","/user/login"
        };

    // 判断本次请求是否需要处理
    boolean check = check(urls, requestURI);

    // 不需处理则直接放行
    if (check) {
      log.info("不需要处理的请求{}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    // 已经登陆则放行
    if (request.getSession().getAttribute("employee") != null) {
      log.info("用户已登录{}", request.getSession().getAttribute("employee"));

      Long empId = (Long) request.getSession().getAttribute("employee");
      BaseContext.setThreadLocal(empId);

      filterChain.doFilter(request, response);
      return;
    }


    // 已经登陆则放行
    if (request.getSession().getAttribute("user") != null) {
      log.info("用户已登录{}", request.getSession().getAttribute("user"));

      Long userId = (Long) request.getSession().getAttribute("user");
      BaseContext.setThreadLocal(userId);

      filterChain.doFilter(request, response);
      return;
    }

    // 未登录返回登录页面,通过输出流向客户端响应数据
    log.info("用户未登录");
    response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    return;
  }

  public boolean check(String[] urls, String requestURI) {
    for (String url : urls) {
      boolean match = PATH_MATCHER.match(url, requestURI);
      if (match) {
        return true;
      }
    }
    return false;
  }
}
