package com.example.demo.signaling;

import com.example.demo.Exceptions.SessionFullException;
import com.example.demo.dto.SignalMessage;
import com.example.demo.dto.SignalType;
import com.example.demo.session.Peer;
import com.example.demo.session.Session;
import com.example.demo.config.WebSocketSender;
import com.example.demo.session.SessionStore;
import com.example.demo.utils.Idgenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SignalingMessageRouter {

    private final SessionStore sessionStore;
    private final WebSocketSender sender;

    public SignalingMessageRouter(SessionStore sessionStore, WebSocketSender sender) {
        this.sessionStore = sessionStore;
        this.sender = sender;
    }

    public void route(SignalMessage msg, WebSocketSession socket)
            throws IOException, SessionFullException {

        switch (msg.getType()) {
            case CREATE_SESSION -> handleCreate(socket);
        }
    }

    private void handleCreate(WebSocketSession socket) throws SessionFullException {

        String sessionId  = Idgenerator.generateId();

        Peer peer = new Peer(socket.getId(), socket);

        Session session= new Session(sessionId, Instant.now());

        session.addPeer(peer);

        sessionStore.save(session);

        SignalMessage response = SignalMessage.builder()
                .type(SignalType.SESSION_CREATED)
                .sessionId(session.getSessionId())
                .build();

        sender.send(socket, response);
    }
}