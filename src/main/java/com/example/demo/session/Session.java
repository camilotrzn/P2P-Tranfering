package com.example.demo.session;

import com.example.demo.Exceptions.SessionFullException;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    private final String sessionId;
    private final Instant createdTime;
    private final Map<String, Peer> peers = new ConcurrentHashMap<>();

    public Session(String sessionId, Instant createdTime) {
        this.sessionId = sessionId;
        this.createdTime = createdTime;
    }

    public void addPeer(Peer peer) throws SessionFullException {
        if (isFull()){
            throw new SessionFullException("Session already have 2 peers.");
        }else{
            peers.put(peer.getPeerId(), peer);
        }
    }

    public String getSessionId() {
        return sessionId;
    }

    public Collection<Peer> getPeers(){
        return peers.values();
    }

    public boolean isFull(){
        return peers.size() >=2;
    }
}
