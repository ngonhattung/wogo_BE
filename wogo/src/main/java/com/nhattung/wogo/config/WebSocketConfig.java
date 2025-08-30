package com.nhattung.wogo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    public static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:8081",
            "http://172.28.64.1:8081",
            "http://127.0.0.1:5500"
    );


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // server -> client
        config.enableSimpleBroker("/topic", "/queue");
        // client -> server
        config.setApplicationDestinationPrefixes("/app");
        // cần enable nếu muốn dùng sendToUser
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // endpoint để client connection với server
        registry.addEndpoint("/ws")
                .setAllowedOrigins(CorsConfig.ALLOWED_ORIGINS.toArray(new String[0]))
                .withSockJS();
    }
}
