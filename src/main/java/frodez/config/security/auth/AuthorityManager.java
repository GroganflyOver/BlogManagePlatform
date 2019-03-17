package frodez.config.security.auth;

import frodez.config.security.settings.SecurityProperties;
import frodez.util.common.EmptyUtil;
import frodez.util.http.URLMatcher;
import frodez.util.spring.context.ContextUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;

/**
 * 权限匹配管理器
 * @author Frodez
 * @date 2018-12-03
 */
@Component
@DependsOn("contextUtil")
public class AuthorityManager implements AccessDecisionManager {

	private Collection<ConfigAttribute> defaultDeniedRoles;

	@PostConstruct
	private void init() {
		SecurityProperties properties = ContextUtil.get(SecurityProperties.class);
		defaultDeniedRoles = Arrays.asList(new SecurityConfig(properties.getAuth().getDeniedRole()));
		Objects.requireNonNull(defaultDeniedRoles);
	}

	/**
	 * 更新权限信息
	 * @author Frodez
	 * @date 2019-03-17
	 */
	public void refresh() {
		synchronized (this) {
			defaultDeniedRoles = null;
			init();
		}
	}

	/**
	 * 判定是否拥有权限<br>
	 * authentication是UserDetailsServiceImpl中添加到GrantedAuthority中的权限信息.<br>
	 * object包含客户端请求的request信息，可转换为HttpServletRequest,方法如下:<br>
	 * request = ((FilterInvocation) object).getHttpRequest()<br>
	 * attributes是DatabaseSecurityMetadataSource的getAttributes方法的返回值.<br>
	 * 如果用户不具有请求的url的权限,抛出AccessDeniedException.<br>
	 * @author Frodez
	 * @date 2018-12-03
	 */
	@Override
	public void decide(Authentication auth, Object object, Collection<ConfigAttribute> permissions)
		throws AccessDeniedException, InsufficientAuthenticationException {
		if (!URLMatcher.needVerify(((FilterInvocation) object).getHttpRequest().getRequestURI())) {
			// 如果是免验证路径,则直接放行
			return;
		}
		if (EmptyUtil.yes(auth.getAuthorities())) {
			throw new AccessDeniedException("无访问权限!");
		}
		// 当包含无访问权限时,直接驳回(此时只有无访问权限一个权限)
		if (permissions.equals(defaultDeniedRoles)) {
			throw new AccessDeniedException("无访问权限!");
		}
		List<String> auths = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors
			.toList());
		for (ConfigAttribute permission : permissions) {
			if (auths.contains(permission.getAttribute())) {
				return;
			}
		}
		// 当token携带权限与资源所需访问权限不符时,驳回
		throw new AccessDeniedException("无访问权限!");
	}

	@Override
	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return true;
	}

}
