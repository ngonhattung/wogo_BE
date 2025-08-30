package com.nhattung.wogo.service.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;

@Component
@Slf4j
public class WebSocketEventListener {

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String user = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";

        log.info("🔌 WebSocket Connected - SessionId: {}, User: {}", sessionId, user);

        // Log thêm thông tin nếu cần
        Map<String, Object> attributes = headerAccessor.getSessionAttributes();
        if (attributes != null) {
            log.info("📋 Session Attributes: {}", attributes);
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String user = headerAccessor.getUser() != null ? headerAccessor.getUser().getName() : "Anonymous";

        log.info("🔌 WebSocket Disconnected - SessionId: {}, User: {}", sessionId, user);
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();

        log.info("📡 WebSocket Subscribed - SessionId: {}, Destination: {}", sessionId, destination);
    }

    @EventListener
    public void handleWebSocketUnsubscribeListener(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("📡 WebSocket Unsubscribed - SessionId: {}", sessionId);
    }
}