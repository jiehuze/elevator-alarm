package com.schedule.config;

import com.schedule.elevator.service.IUserTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private IUserTokenService userTokenService;

    // 配置无需认证的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/elevator/**",
            "/actuator/health"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 检查是否为白名单路径
        String requestURI = request.getRequestURI();
        boolean isWhiteListed = WHITE_LIST.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

        if (isWhiteListed) {
            System.out.println("白名单路径：" + requestURI);
            // 白名单路径，直接放行，不设置 Authentication
            filterChain.doFilter(request, response);
            return;
        }

        // 非白名单路径，尝试解析 Token
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && userTokenService.validateToken(token)) {
            // Token 有效，设置 Authentication 到 SecurityContext
            Claims claims = userTokenService.parseClaims(token); // 你需要在 service 中提供这个方法
            String userId = claims.getSubject();

            // 创建 Authentication 对象（可以包含用户信息）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // 如果 Token 无效，不设置 Authentication，让 Security 的权限判断来处理

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}