# 会话管理

## 防火墙

### ip白名单

#### 指定ip可以不登录

```
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		
		.antMatchers("/ip1").hasIpAddress("127.0.0.1")
```

#### 禁止ip访问

用Filter 实现、或者用HandlerInterceptor 实现

### StrictHttpFirewall

spring security 默认使用StrictHttpFirewall限制用户请求

#### method

缺省被允许的`HTTP method`有 [`DELETE`, `GET`, `HEAD`, `OPTIONS`, `PATCH`, `POST`, `PUT`]

#### URI

**在其`requestURI`/`contextPath`/`servletPath`/`pathInfo`中，必须不能包含以下字符串序列之一 :**

```
["//","./","/…/","/."]
```



#### 分号

```
;或者%3b或者%3B
// 禁用规则
setAllowSemicolon(boolean)
```



#### 斜杠

```
%2f`或者`%2F
// 禁用规则
setAllowUrlEncodedSlash(boolean)
```

#### 反斜杠

```
\或者%5c或者%5B
// 禁用规则
setAllowBackSlash(boolean)
```

#### 英文句号

```
%2e或者%2E
// 禁用规则
setAllowUrlEncodedPeriod(boolean)
```

#### 百分号

```
%25
// 禁用规则
setAllowUrlEncodedPercent(boolean)
```

#### 防火墙与sql注入

' ; -- % 多数非法字符已经在请求的参数上被禁用

为啥用户名不能有特殊字符

preparestatement 

awf前端拦截

## 自定义配置

### 指定登录的action

```
.loginProcessingUrl("/login")
```
### 指定登录成功后的页面

			//直接访问登录页面时返回的地址,如果访问的是登录页的话返回指定的地址
			.defaultSuccessUrl("/",true)
			 //必须返回指定地址
			.defaultSuccessUrl("/",true)

### 指定错误页

		//指定错误页
		.failureUrl("/error.html?error1")
### 注销登录

#### 开启CSRF之后 需要使用post请求退出接口

#### 

```
<a href="/logout">GET logout</a>
<br />
<form action="/logout" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    <input type="submit" value="POST Logout"/>
</form>
```

#### 默认方式 get /logout



#### 自定义url

```
		.and()
		.logout()
		.logoutUrl("/out")
```

### 增加退出处理器

```

		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("退出1");
			}
		})
		
		
		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("退出2");
			}
		})
```

### 登录成功处理器

不同角色 跳转到不同页面

		.successHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// TODO Auto-generated method stub
				
				System.out.println("登录成功1");
				// 根据权限不同，跳转到不同页面
				request.getSession().getAttribute(name)
				request.getRequestDispatcher("").forward(request, response);
			}
		})
其中 Authentication 参数包含了 用户权限信息

### 登录失败处理器

```
		.failureHandler(new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				// TODO Auto-generated method stub
				exception.printStackTrace();
				request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
			}
		})
```

可以限制登录错误次数



## 访问权限

访问权限可以配置URL匹配用户角色或权限

		http.authorizeRequests()
		.antMatchers("/admin/**").hasRole("admin")
		.antMatchers("/user/**").hasRole("user")
	@Bean



### 匹配顺序

security像shiro一样，权限匹配有顺序，比如不能把.anyRequest().authenticated()写在其他规则前面

### 权限继承

	RoleHierarchy roleHierarchy() {
		
		RoleHierarchyImpl impl = new RoleHierarchyImpl();
		impl.setHierarchy("ROLE_admin > ROLE_user");
		
		return impl;
		
	}

## 权限控制细粒度注解

## 角色匹配

### 配置类

```
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled = true)
```

### **securedEnabled = true** 

方法验证

```
	@GetMapping("/hi_admin")
	@Secured({"ROLE_admin","ROLE_user"})
	public Authentication hi() {
```

开启简单验证，之验证单一角色是否持有



### **prePostEnabled = true** 

支持更复杂的角色匹配，比如必须同时包含两个角色

​	**需包含user角色**

```
	@PreAuthorize("hasRole('ROLE_user')")

```

**需包含user或admin角色**

```
	@PreAuthorize("haAnyRole('ROLE_admin','ROLE_user')")
```



**同时需包含user和admin角色**

```
	@PreAuthorize("hasRole('ROLE_admin') AND hasRole('ROLE_user')")
```



### 根据方法返回值判断是否有权限

```
	@PostAuthorize("returnObject==1")
```





### 方法拦截

```
	@GetMapping("/hi")
	@PreAuthorize("hasRole('ROLE_admin')")
	public String hi() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi";
	}
	
	
	@PreAuthorize("hasRole('ROLE_user')")
	@GetMapping("/hiUser")
	public String hiuser() {
	//	UserDetailsServiceAutoConfiguration
		System.out.println("来啦老弟~！");
		return "hi";
	}
```

### 获取用户权限信息和UserDetails

```
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	
		authentication.getPrincipal()
```



## 图形验证码

目的：防机器暴力登陆

### Kaptcha 

| Constant                         | 描述                                                         | 默认值                                                |
| -------------------------------- | ------------------------------------------------------------ | ----------------------------------------------------- |
| kaptcha.border                   | 图片边框，合法值：yes , no                                   | yes                                                   |
| kaptcha.border.color             | 边框颜色，合法值： r,g,b (and optional alpha) 或者 white,black,blue. | black                                                 |
| kaptcha.image.width              | 图片宽                                                       | 200                                                   |
| kaptcha.image.height             | 图片高                                                       | 50                                                    |
| kaptcha.producer.impl            | 图片实现类                                                   | com.google.code.kaptcha.impl.DefaultKaptcha           |
| kaptcha.textproducer.impl        | 文本实现类                                                   | com.google.code.kaptcha.text.impl.DefaultTextCreator  |
| kaptcha.textproducer.char.string | 文本集合，验证码值从此集合中获取                             | abcde2345678gfynmnpwx                                 |
| kaptcha.textproducer.char.length | 验证码长度                                                   | 5                                                     |
| kaptcha.textproducer.font.names  | 字体                                                         | Arial, Courier                                        |
| kaptcha.textproducer.font.size   | 字体大小                                                     | 40px.                                                 |
| kaptcha.textproducer.font.color  | 字体颜色，合法值： r,g,b  或者 white,black,blue.             | black                                                 |
| kaptcha.textproducer.char.space  | 文字间隔                                                     | 2                                                     |
| kaptcha.noise.impl               | 干扰实现类                                                   | com.google.code.kaptcha.impl.DefaultNoise             |
| kaptcha.noise.color              | 干扰 颜色，合法值： r,g,b 或者 white,black,blue.             | black                                                 |
| kaptcha.obscurificator.impl      | 图片样式：<br />水纹 com.google.code.kaptcha.impl.WaterRipple <br /> 鱼眼 com.google.code.kaptcha.impl.FishEyeGimpy <br /> 阴影 com.google.code.kaptcha.impl.ShadowGimpy | com.google.code.kaptcha.impl.WaterRipple              |
| kaptcha.background.impl          | 背景实现类                                                   | com.google.code.kaptcha.impl.DefaultBackground        |
| kaptcha.background.clear.from    | 背景颜色渐变，开始颜色                                       | light grey                                            |
| kaptcha.background.clear.to      | 背景颜色渐变， 结束颜色                                      | white                                                 |
| kaptcha.word.impl                | 文字渲染器                                                   | com.google.code.kaptcha.text.impl.DefaultWordRenderer |
| kaptcha.session.key              | session key                                                  | KAPTCHA_SESSION_KEY                                   |
| kaptcha.session.date             | session date                                                 | KAPTCHA_SESSION_DATE                                  |



作者：撸帝
链接：https://www.jianshu.com/p/a3525990cd82
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

```
<!-- https://mvnrepository.com/artifact/com.github.penggle/kaptcha -->
<dependency>
    <groupId>com.github.penggle</groupId>
    <artifactId>kaptcha</artifactId>
    <version>2.3.2</version>
</dependency>

```

### 添加一个前置Filter

```
	http.addFilterBefore(new CodeFilter(), UsernamePasswordAuthenticationFilter.class);
```



```java

public class CodeFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		String uri = req.getServletPath();


		if(uri.equals("/login") && req.getMethod().equalsIgnoreCase("post")) {

		
			String sessionCode = req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY).toString();
			String formCode = req.getParameter("code").trim();
				
			if(StringUtils.isEmpty(formCode)) {
				throw new RuntimeException("验证码不能为空");
			}
			if(sessionCode.equalsIgnoreCase(formCode)) {
				
				System.out.println("验证通过");
				
			}		
					System.out.println(req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY));
			throw new AuthenticationServiceException("xx");
		}

		chain.doFilter(request, response);
		
	}

```

显示验证码的Controller

```
	@GetMapping("/kaptcha")
    public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        String capText = captchaProducer.createText();
        
        
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = captchaProducer.createImage(capText);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }
```



### 配置类

```
package com.mashibing.admin;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class Kaconfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha(){
        DefaultKaptcha captchaProducer = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.image.width", "310");
        properties.setProperty("kaptcha.image.height", "240");
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        properties.setProperty("kaptcha.session.key", "code");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
    //    properties.setProperty("kaptcha.textproducer.char.string", "678");
        properties.setProperty("kaptcha.obscurificator.impl", "com.google.code.kaptcha.impl.ShadowGimpy");
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);
        return captchaProducer;

    }
}

```



### 短信验证码

### 人机交互验证

