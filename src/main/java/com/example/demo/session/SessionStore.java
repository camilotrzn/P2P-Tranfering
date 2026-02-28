package com.example.demo.session;

import com.example.demo.utils.Idgenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> socketToSession = new ConcurrentHashMap<>();

    public void save(Session session){
        sessions.put(session.getSessionId(), session);
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void addPeerToSession(String sessionId, Peer peer) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.addPeer(peer);
            socketToSession.put(peer.getWebSocketSession().getId(), sessionId);
        } else {
            throw new IllegalArgumentException("Session not found");
        }
    }

    public void removePeerBySocketId(String socketId) {

        String sessionId = socketToSession.remove(socketId);
        if (sessionId == null) return;

        Session session = sessions.get(sessionId);
        if (session == null) return;

        session.removePeerBySocketId(socketId);

        if (session.isEmpty()) {
            sessions.remove(sessionId);
        }
    }

    public Session getBySocketId(String socketId){
        if (socketId == null) return null;

        String sessionId = socketToSession.get(socketId);
        if (sessionId == null) return null;

        return sessions.get(sessionId);
    }

    public Optional<Session> get(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public List<String> getAllSessionIds() {
        return new ArrayList<>(sessions.keySet());
    }

    public List<Session> getAllSessions() {
        return (List<Session>) sessions;
    }

    public void mapSocketToSession(String socketId, String sessionId) {
        socketToSession.put(socketId, sessionId);
    }
}
