package com.socialmeet.backend.config;

import com.socialmeet.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手拦截器
 * 用于验证JWT token并设置用户ID
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        try {
            // 从查询参数中获取token
            String query = request.getURI().getQuery();
            if (query == null || !query.contains("token=")) {
                log.warn("WebSocket连接缺少token参数");
                return false;
            }

            String token = query.substring(query.indexOf("token=") + 6);
            if (token.contains("&")) {
                token = token.substring(0, token.indexOf("&"));
            }

            // 验证token并获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("WebSocket连接token无效");
                return false;
            }

            // 将用户ID存储到WebSocket会话属性中
            attributes.put("userId", userId.toString());
            log.info("WebSocket握手成功 - userId: {}", userId);
            return true;

        } catch (Exception e) {
            log.error("WebSocket握手失败", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket握手后处理失败", exception);
        }
    }
}
