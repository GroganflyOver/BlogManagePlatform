package frodez.util.http;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import frodez.config.cache.CacheProperties;
import frodez.config.security.settings.SecurityProperties;
import frodez.constant.setting.DefTime;
import frodez.constant.setting.PropertyKey;
import frodez.util.spring.context.ContextUtil;
import frodez.util.spring.properties.PropertyUtil;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;

@Component
@DependsOn(value = { "propertyUtil", "contextUtil" })
public class URLMatcher {

	/**
	 * spring路径匹配器
	 */
	private static PathMatcher matcher;

	private static List<String> permitPaths;

	/**
	 * url匹配缓存
	 */
	private static Cache<String, Boolean> URL_CACHE;

	@PostConstruct
	private void init() {
		SecurityProperties securityProperties = ContextUtil.get(SecurityProperties.class);
		CacheProperties cacheProperties = ContextUtil.get(CacheProperties.class);
		matcher = ContextUtil.get(PathMatcher.class);
		permitPaths = new ArrayList<>();
		for (String path : securityProperties.getAuth().getPermitAllPath()) {
			permitPaths.add(PropertyUtil.get(PropertyKey.Web.BASE_PATH) + path);
		}
		URL_CACHE = CacheBuilder.newBuilder().maximumSize(cacheProperties.getUrlMatcher().getMaxSize())
			.expireAfterAccess(cacheProperties.getUrlMatcher().getTimeout(), DefTime.UNIT).build();
	}

	/**
	 * 判断url是否需要验证,url为带有根路径的url<br>
	 * <strong>true为需要验证,false为不需要验证</strong><br>
	 * <strong>建议url不要带入任何path类型参数,以提高性能!</strong>
	 * @author Frodez
	 * @date 2019-01-06
	 */
	public static boolean needVerify(String url) {
		Boolean result = URL_CACHE.getIfPresent(url);
		if (result != null) {
			return result;
		}
		for (String path : permitPaths) {
			if (matcher.match(path, url)) {
				URL_CACHE.put(url, false);
				return false;
			}
		}
		URL_CACHE.put(url, true);
		return true;
	}

}