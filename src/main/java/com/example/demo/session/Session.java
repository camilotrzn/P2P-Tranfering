package com.example.demo.session;

import lombok.Getter;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    @Getter
    private final String sessionId;
    private final Instant createdTime;
    private final Map<String, Peer> peers = new ConcurrentHashMap<>();

    public Session(String sessionId, Instant createdTime) {
        this.sessionId = sessionId;
        this.createdTime = createdTime;
    }

    public void addPeer(Peer peer) {
            peers.put(peer.getPeerId(), peer);
    }

    public Peer getOtherPeer(String peerId) {
        return peers.values().stream()
                .findFirst()
                .filter(p -> !p.getPeerId().equals(peerId))
                .orElse(null);
    }

    public Collection<Peer> getPeers(){
        return peers.values();
    }

    public void removePeerBySocketId(String socketId) {
        peers.remove(socketId);
    }

    public boolean isFull(){
        return peers.size() >=2;
    }

    public boolean isEmpty() {
        return peers.isEmpty();
    }
}
