package com.example.demo.session;

import com.example.demo.utils.Idgenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStore {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void save(Session session){
        sessions.put(session.getSessionId(), session);
    }

    public Optional<Session> get(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}
