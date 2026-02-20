package com.example.demo.signaling;

import com.example.demo.dto.SignalMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Component
public class SignalingHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final SignalingMessageRouter router;

    public SignalingHandler(SignalingMessageRouter router) {
        this.router = router;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
        throws Exception{
        SignalMessage signal =
                mapper.readValue(message.getPayload(), SignalMessage.class);

        router.route(signal, session);
    }
}
