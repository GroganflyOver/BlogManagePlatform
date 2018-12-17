package info.frodez.config.security.realization;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import info.frodez.dao.result.user.PermissionInfo;

/**
 * @author Frodez
 * @date 2018-11-14
 */
public final class AuthorityUtil {

	/**
	 * 生成权限信息
	 * @author Frodez
	 * @param authorities 权限信息
	 * @date 2018-11-21
	 */
    public static List<GrantedAuthority> createGrantedAuthorities(List<PermissionInfo> authorities) {
        return authorities.stream().map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());
    }

    /**
	 * 生成权限信息
	 * @author Frodez
	 * @param authorities 权限信息
	 * @date 2018-11-21
	 */
    public static List<GrantedAuthority> createGrantedAuthorities(String... authorities) {
        return Stream.of(authorities).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
	 * 获取权限信息
	 * @author Frodez
	 * @param UserDetails 用户信息
	 * @date 2018-11-21
	 */
    public static String[] getAuthorities(UserDetails user) {
        return user.getAuthorities().stream().map((iter) -> {return iter.getAuthority();}).toArray(String[]::new);
    }
	
}
