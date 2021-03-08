package com.lisz.filter;

import com.lisz.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/**")
@Component
public class AuthFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.println("request 来啦");
		HttpServletRequest req = (HttpServletRequest)request;

		// 自动续、手动续Token。自动续就是下发token之后，客户端替换原来的，然后那新的请求；或者过一定的时间间隔手动续
		String token = req.getHeader("token");
		System.out.println(token);
		if (!StringUtils.hasText(token)){
			System.out.println("没有带Token，所以还没登录，直接返回，不让访问");
			return;
		}
		// Token 解析不出来就会报错。解析出来之后可以查看体信息得到在登录的时候就下发的角色和权限，再看看被访问的URL是不是被授权了
		String parseToken = JwtUtil.parseToken(token);
		if (!StringUtils.hasText(parseToken)){
			System.out.println("Token不正确，返回");
			return;
		}
		chain.doFilter(request, response);
		System.out.println("response 回去");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("AuthFilter 启动成功");
	}
}
