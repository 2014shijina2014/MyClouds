package com.gitee.myclouds.admin.web.common.filters;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.gitee.myclouds.common.MyCacheCxt;
import com.gitee.myclouds.toolbox.session.data.CurUser;
import com.gitee.myclouds.toolbox.util.FilterUtil;
import com.gitee.myclouds.toolbox.util.MyCons;
import com.gitee.myclouds.toolbox.util.WebCxt;

import cn.hutool.core.util.StrUtil;

/**
 * Admin授权权限过滤器
 * 
 * <p>
 * 主要控制登录后的越权访问
 * 
 * @author xiongchun
 *
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = { "/*" })
public class AuthFilter implements Filter {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private MyCacheCxt myCacheCxt;

	// 排除列表
	private String[] excludeKeysArray;
	//启用开关
	private String isEnable;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//让SpringBean能注入到servlet上下文环境中
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, filterConfig.getServletContext());
		//提示：在这里初始化Filter的配置信息，性价比较高(避免每次过滤都加载配置)。但配置修改后，需要重启这个Server以创新加载配置。
		isEnable = myCacheCxt.getParamValue("authfilter_is_enable", MyCons.YesOrNo.YES.getValue().toString());
		excludeKeysArray = StrUtil.split(myCacheCxt.getParamValue("authfilter_exclude_keys"), ",");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
		HttpSession httpSession = httpServletRequest.getSession();
		if (!StrUtil.equals(isEnable, MyCons.YesOrNo.YES.getValue().toString())) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		if (FilterUtil.checkExcludes(httpServletRequest.getRequestURI(), excludeKeysArray)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		//未登录用户的跳转处理在loginFilter完成，这里不处理
		CurUser curUser = WebCxt.getCurUser(httpSession);
		if (curUser == null) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}
		
		String uri = httpServletRequest.getRequestURI();
		uri = StrUtil.startWith(uri, "/")&&!StrUtil.equals(uri, "/") ?  StrUtil.subAfter(uri, "/", false) : uri;
		boolean isMember = false;
		List<String> roleIds = curUser.getRoleIds();
		for (String roleId : roleIds) {
			String key = MyCons.CacheKeyOrPrefix.RoleAuth.getValue() + ":" + roleId;
			isMember = stringRedisTemplate.opsForSet().isMember(key, uri);
			if (isMember) {
				break;
			}
		}
		if (isMember) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			String msg = StrUtil.format("安全组件鉴权失败：访问权限受限，资源[{}]未授权(或未找到)。", uri);
			logger.error(msg);
			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
		}

	}

	@Override
	public void destroy() {

	}

}
