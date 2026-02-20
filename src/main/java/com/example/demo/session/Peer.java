package com.example.demo.session;

import lombok.Lombok;
import org.springframework.web.socket.WebSocketSession;

import java.net.http.WebSocket;

public class Peer {
    private final String peerId;
    private final WebSocketSession webSocketSession;

    public Peer(String peerId, WebSocketSession webSocketSession){
        this.peerId = peerId;
        this.webSocketSession = webSocketSession;
    }

    public String getPeerId() {
        return peerId;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }
}
