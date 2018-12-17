package info.frodez.config.security.realization;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import info.frodez.config.security.settings.SecurityProperties;

/**
 * jwt验证过滤器
 * @author Frodez
 * @date 2018-11-21
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private SecurityProperties properties;

    @Override
    protected void doFilterInternal(HttpServletRequest request
    	, HttpServletResponse response, FilterChain chain) 
    	throws ServletException, IOException {
        String authToken = request.getHeader(properties.getJwt().getHeader());
        if(authToken != null) {
        	//将携带的token还原成用户信息
        	UserDetails user = jwtTokenUtil.verify(authToken);
            if (user != null && SecurityContextHolder
            	.getContext().getAuthentication() == null) {
            	UsernamePasswordAuthenticationToken authentication = new 
            		UsernamePasswordAuthenticationToken(
            		user, null, user.getAuthorities());
                authentication.setDetails(
                	new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                	.setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
	
}
