package com.example.socialmeet.filter;

import com.example.socialmeet.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // 跳过不需要认证的路径
        String requestPath = request.getRequestURI();
        System.out.println("请求路径: " + requestPath);
        
        // 只跳过明确的公开路径
        if (requestPath.startsWith("/api/auth/") || 
            requestPath.equals("/api/health") ||
            requestPath.startsWith("/api/admin/database/fix/") ||  // 数据库修复接口
            requestPath.startsWith("/h2-console/") ||
            requestPath.startsWith("/api-docs/") ||
            requestPath.startsWith("/swagger-ui/") ||
            requestPath.equals("/swagger-ui.html") ||
            requestPath.startsWith("/actuator/")) {
            System.out.println("跳过认证的公开路径: " + requestPath);
            filterChain.doFilter(request, response);
            return;
        }
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            System.out.println("=== JWT调试信息 ===");
            System.out.println("Authorization Header: " + authorizationHeader);
            System.out.println("JWT Token: " + jwt);
            System.out.println("Token长度: " + (jwt != null ? jwt.length() : 0));
            System.out.println("Token包含点号数量: " + (jwt != null ? jwt.split("\\.").length - 1 : 0));
            System.out.println("==================");
            
            // 检查token是否为空或格式不正确
            if (jwt == null || jwt.trim().isEmpty()) {
                System.err.println("JWT token为空");
            } else if (jwt.split("\\.").length != 3) {
                System.err.println("JWT token格式不正确，应该有3个部分，实际有: " + jwt.split("\\.").length);
            } else {
                try {
                    username = jwtUtil.getUsernameFromToken(jwt);
                    System.out.println("从Token中提取的用户名: " + username);
                } catch (Exception e) {
                    System.err.println("JWT token validation failed: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("=== JWT调试信息 ===");
            System.out.println("Authorization Header: " + authorizationHeader);
            System.out.println("请求路径: " + requestPath);
            System.out.println("==================");
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
