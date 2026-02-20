package com.example.demo.config;

import com.example.demo.dto.SignalMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class WebSocketSender {

    private final ObjectMapper objectMapper;

    public void send(WebSocketSession socket, SignalMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            socket.sendMessage(new TextMessage(json));
        } catch (IOException e) {
            throw new RuntimeException("WebSocket send failed", e);
        }
    }
}