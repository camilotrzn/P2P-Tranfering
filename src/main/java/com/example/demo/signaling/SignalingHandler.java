package com.example.demo.signaling;

import com.example.demo.dto.SignalMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class SignalingHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final SignalingMessageRouter router;

    public SignalingHandler(ObjectMapper mapper, SignalingMessageRouter router) {
        this.mapper = mapper;
        this.router = router;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session,
                                     TextMessage message)
            throws Exception {

        SignalMessage signal =
                mapper.readValue(message.getPayload(), SignalMessage.class);

        router.route(signal, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {

        router.handleDisconnect(session);
    }
}
