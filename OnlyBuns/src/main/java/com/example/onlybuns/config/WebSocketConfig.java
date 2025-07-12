package com.example.onlybuns.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Uključuje WebSocket message handling, podržan "message broker"-om
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registruje "/ws" endpoint, koji će klijenti koristiti da se konektuju na WebSocket server.
        // withSockJS() pruža fallback opcije za pretraživače koji ne podržavaju WebSocket.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Definiše da poruke čija destinacija počinje sa "/app" treba da se rutiraju
        // ka @MessageMapping anotiranim metodama u kontrolerima.
        registry.setApplicationDestinationPrefixes("/app");

        // Definiše da poruke čija destinacija počinje sa "/topic" ili "/user" treba da se rutiraju
        // ka message brokeru. Broker onda šalje poruke svim klijentima koji su se pretplatili
        // na određeni topic.
        // "/user" se koristi za slanje poruka specifičnom korisniku.
        registry.enableSimpleBroker("/topic", "/user");
        registry.setUserDestinationPrefix("/user");
    }
}