package com.schedule.elevator.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.schedule.common.BaseResponse;
import com.schedule.elevator.service.IUserTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private IUserTokenService userTokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    // 配置无需认证的路径
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/elevator/sys-user/**",
            "/elevator/**",
            "/actuator/health"
    );

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

        if (!StringUtils.hasText(token) || !userTokenService.isTokenValidForUser(null, token)) {
            System.out.println("无效的token：" + token);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的token");
            return;
        }

        if (StringUtils.hasText(token) && userTokenService.validateToken(token)) {
            // Token 有效，设置 Authentication 到 SecurityContext
            Claims claims = userTokenService.parseClaims(token); // 你需要在 service 中提供这个方法
            String userId = claims.getSubject();

            // 创建 Authentication 对象（可以包含用户信息）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        BaseResponse baseResponse = new BaseResponse(status, message, null, null);
        response.getWriter().write(objectMapper.writeValueAsString(baseResponse));
    }
}