package com.example.demo.signaling;

import com.example.demo.dto.SignalMessage;
import com.example.demo.dto.SignalType;
import com.example.demo.session.Peer;
import com.example.demo.config.WebSocketSender;
import com.example.demo.session.Session;
import com.example.demo.session.SessionStore;
import com.example.demo.utils.Idgenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.DatagramSocket;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class SignalingMessageRouter {

    private final SessionStore sessionStore;
    private final WebSocketSender sender;

    public SignalingMessageRouter(SessionStore sessionStore,
                                  WebSocketSender sender) {
        this.sessionStore = sessionStore;
        this.sender = sender;
    }

    public void route(SignalMessage msg, WebSocketSession socket) throws IOException {

        try {
            switch (msg.getType()) {
                case CREATE_SESSION -> handleCreate(socket);
                case JOIN_SESSION -> handleJoin(msg, socket);
                case FETCH_SESSIONS -> handleSessionListRequest(socket);
                case OFFER, ANSWER, ICE_CANDIDATE -> forwardToPeer(msg);
                case PEERID_REQUEST -> returnPeerId(socket);
                default -> sendError(socket, "Unknown message type");
            }

        } catch (Exception e) {
            System.out.println("SessionFullException: " + e.getMessage());
            sendError(socket, e.getMessage());

        }
    }

    private void handleCreate(WebSocketSession socket)
            throws IOException {

        String sessionId = Idgenerator.generateId();

        Peer peer = new Peer(socket.getId(), socket);

        Session session = new Session(sessionId, Instant.now());
        session.addPeer(peer);

        sessionStore.save(session);
        sessionStore.mapSocketToSession(socket.getId(), sessionId);

        SignalMessage response = SignalMessage.builder()
                .type(SignalType.SESSION_CREATED)
                .sessionId(sessionId)
                .fromPeer(peer.getPeerId())
                .build();

        sender.send(socket, response);
    }

    private void handleJoin(SignalMessage msg,
                            WebSocketSession socket)
            throws IOException {

        String sessionId = msg.getSessionId();

        Optional<Session> optionalSession =
                sessionStore.get(sessionId);

        if (optionalSession.isEmpty()) {
            sendError(socket, "Session not found");
            return;
        }

        Session session = optionalSession.get();


        if (session.isFull()) {
            sendError(socket, "Session is full");
            return;
        }

        Peer peer = new Peer(socket.getId(), socket);

        sessionStore.mapSocketToSession(socket.getId(), sessionId);

        session.addPeer(peer);

        SignalMessage response = SignalMessage.builder()
                .type(SignalType.JOIN_SUCCESS)
                .sessionId(sessionId)
                .fromPeer(peer.getPeerId())
                .build();

        sender.send(socket, response);
    }

    private void forwardToPeer(SignalMessage msg)
            throws IOException {

        Optional<Session> optionalSession =
                sessionStore.get(msg.getSessionId());

        if (optionalSession.isEmpty()) {
            return;
        }

        Session session = optionalSession.get();

        Peer target = session.getOtherPeer(msg.getFromPeer());

        if (target == null) {
            return;
        }

        WebSocketSession targetSocket =
                target.getWebSocketSession();

        if (targetSocket.isOpen()) {
            sender.send(targetSocket, msg);
        }
    }


    public void handleDisconnect(WebSocketSession socket) {

        Optional<Session> optionalSession =
                Optional.ofNullable(sessionStore.getBySocketId(socket.getId()));

        if (optionalSession.isEmpty()) return;

        Session session = optionalSession.get();

        session.removePeerBySocketId(socket.getId());

        if (session.getPeers().isEmpty()) {
            sessionStore.remove(session.getSessionId());
        } else {
            notifyPeerLeft(session);
        }
    }

    private void notifyPeerLeft(Session session) {

        for (Peer peer : session.getPeers()) {

            SignalMessage leftMsg = SignalMessage.builder()
                    .type(SignalType.PEER_LEFT)
                    .sessionId(session.getSessionId())
                    .build();

            sender.send(peer.getWebSocketSession(), leftMsg);
        }
    }

    // Method to handle the request for session list
    public void handleSessionListRequest(WebSocketSession socket) throws IOException {
        List<String> sessionIds = sessionStore.getAllSessionIds();
        SignalMessage response = SignalMessage.builder()
                .type(SignalType.SESSION_LIST)
                .sessionIds(sessionIds)
                .build();
        sender.send(socket, response);
    }

    // Method to retrieve all active session IDs
    private List<String> getAllSessionIds() {
        return sessionStore.getAllSessions().stream()
                .map(Session::getSessionId)
                .collect(Collectors.toList());
    }

    private void sendError(WebSocketSession socket,
                           String error)
            throws IOException {

        sender.send(socket,
                SignalMessage.builder()
                        .type(SignalType.ERROR)
                        .error(error)
                        .build());
    }

    private void returnPeerId(WebSocketSession socket) {

        SignalMessage response = SignalMessage.builder()
                .type(SignalType.PEERID_RESPONSE) // <- mejor tipo
                .fromPeer(socket.getId())
                .build();

        sender.send(socket, response);
    }
}

