package com.example.myapplication.websocket;

import android.util.Log;

import com.example.myapplication.auth.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * 聊天消息WebSocket管理器
 * 使用STOMP协议连接后端WebSocket服务
 * 接收实时聊天消息推送
 */
public class MessageWebSocketManager {
    private static final String TAG = "MessageWebSocket";

    private static final String WS_URL = "ws://10.0.2.2:8080/ws"; // 模拟器使用10.0.2.2访问宿主机
    // private static final String WS_URL = "ws://localhost:8080/ws"; // 真机使用

    private WebSocket webSocket;
    private OkHttpClient client;
    private AuthManager authManager;
    private List<MessageListener> listeners = new ArrayList<>();

    private boolean isConnected = false;
    private boolean shouldReconnect = true;

    /**
     * 消息监听器
     */
    public interface MessageListener {
        void onNewMessage(JSONObject message);
        void onConnected();
        void onDisconnected();
    }

    public MessageWebSocketManager(AuthManager authManager) {
        this.authManager = authManager;

        // 创建OkHttp客户端
        client = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    /**
     * 连接WebSocket
     */
    public void connect() {
        if (isConnected) {
            Log.w(TAG, "WebSocket已连接，跳过重复连接");
            return;
        }

        Long userId = authManager.getUserId();
        if (userId == null) {
            Log.e(TAG, "用户未登录，无法连接WebSocket");
            return;
        }

        String token = authManager.getToken();
        if (token == null || token.trim().isEmpty()) {
            Log.e(TAG, "Token为空，无法连接WebSocket");
            return;
        }

        Log.i(TAG, "开始连接WebSocket - userId: " + userId);

        // 添加token参数到WebSocket URL
        String wsUrlWithToken = WS_URL + "?token=" + token;
        Request request = new Request.Builder()
                .url(wsUrlWithToken)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.i(TAG, "WebSocket连接已建立");
                isConnected = true;

                // 发送STOMP CONNECT帧
                sendStompConnect(userId);

                // 通知监听器
                notifyConnected();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "收到WebSocket消息: " + text);

                // 处理STOMP消息
                handleStompMessage(text);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.i(TAG, "WebSocket连接正在关闭 - code: " + code + ", reason: " + reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.i(TAG, "WebSocket连接已关闭 - code: " + code + ", reason: " + reason);
                isConnected = false;
                notifyDisconnected();

                // 自动重连
                if (shouldReconnect) {
                    reconnect();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket连接失败", t);
                isConnected = false;
                notifyDisconnected();

                // 自动重连
                if (shouldReconnect) {
                    reconnect();
                }
            }
        });
    }

    /**
     * 发送STOMP CONNECT帧
     */
    private void sendStompConnect(Long userId) {
        String connectFrame = "CONNECT\n" +
                "accept-version:1.1,1.0\n" +
                "heart-beat:10000,10000\n" +
                "\n" +
                "\u0000";

        boolean sent = webSocket.send(connectFrame);
        Log.d(TAG, "发送STOMP CONNECT帧 - 成功: " + sent);

        // 订阅消息队列
        subscribeToMessages(userId);
    }

    /**
     * 订阅消息队列
     */
    private void subscribeToMessages(Long userId) {
        String destination = "/queue/messages/" + userId;
        String subscribeFrame = "SUBSCRIBE\n" +
                "id:sub-0\n" +
                "destination:" + destination + "\n" +
                "\n" +
                "\u0000";

        boolean sent = webSocket.send(subscribeFrame);
        Log.i(TAG, "订阅消息队列 - destination: " + destination + ", 成功: " + sent);
    }

    /**
     * 处理STOMP消息
     */
    private void handleStompMessage(String message) {
        try {
            // 解析STOMP帧
            if (message.startsWith("CONNECTED")) {
                Log.i(TAG, "收到STOMP CONNECTED帧");
                return;
            }

            if (message.startsWith("MESSAGE")) {
                // 提取消息体
                String[] lines = message.split("\n\n", 2);
                if (lines.length < 2) {
                    Log.w(TAG, "STOMP MESSAGE帧格式错误");
                    return;
                }

                String body = lines[1].replace("\u0000", "");
                Log.i(TAG, "收到聊天消息: " + body);

                // 解析JSON消息
                JSONObject jsonMessage = new JSONObject(body);
                String type = jsonMessage.optString("type");

                if ("CHAT_MESSAGE".equals(type)) {
                    // 通知监听器
                    notifyNewMessage(jsonMessage);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "解析STOMP消息失败", e);
        }
    }

    /**
     * 断开WebSocket
     */
    public void disconnect() {
        Log.i(TAG, "断开WebSocket连接");
        shouldReconnect = false;

        if (webSocket != null) {
            webSocket.close(1000, "客户端主动断开");
            webSocket = null;
        }

        isConnected = false;
    }

    /**
     * 自动重连
     */
    private void reconnect() {
        if (!shouldReconnect) {
            Log.i(TAG, "不允许自动重连");
            return;
        }

        Log.i(TAG, "5秒后尝试重新连接WebSocket...");
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            connect();
        }, 5000);
    }

    /**
     * 添加消息监听器
     */
    public void addMessageListener(MessageListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 移除消息监听器
     */
    public void removeMessageListener(MessageListener listener) {
        listeners.remove(listener);
    }

    /**
     * 通知新消息
     */
    private void notifyNewMessage(JSONObject message) {
        for (MessageListener listener : listeners) {
            listener.onNewMessage(message);
        }
    }

    /**
     * 通知连接成功
     */
    private void notifyConnected() {
        for (MessageListener listener : listeners) {
            listener.onConnected();
        }
    }

    /**
     * 通知连接断开
     */
    private void notifyDisconnected() {
        for (MessageListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    /**
     * 检查是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }
}
