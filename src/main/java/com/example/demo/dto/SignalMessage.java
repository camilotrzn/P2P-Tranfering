package com.example.demo.dto;

import lombok.*;
import tools.jackson.databind.JsonNode;

import java.util.List;


@Getter
@Builder
public class SignalMessage {
    private SignalType type;
    private String sessionId;
    private String fromPeer;
    private String toPeer;
    private JsonNode payload;
    private String error;
    private List<String> sessionIds;

    public SignalMessage(SignalType type, String sessionId, String fromPeer, String toPeer, JsonNode payload, String error, List<String> sessionIds) {
        this.type = type;
        this.sessionId = sessionId;
        this.fromPeer = fromPeer;
        this.toPeer = toPeer;
        this.payload = payload;
        this.error = error;
        this.sessionIds = sessionIds;
    }
}

