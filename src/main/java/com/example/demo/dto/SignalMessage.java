package com.example.demo.dto;

import lombok.*;
import tools.jackson.databind.JsonNode;


@Getter
@Builder
public class SignalMessage {
    private SignalType type;
    private String sessionId;
    private String fromPeer;
    private String toPeer;
    private JsonNode payload;

    public SignalMessage(SignalType type, String sessionId, String fromPeer, String toPeer, JsonNode payload) {
        this.type = type;
        this.sessionId = sessionId;
        this.fromPeer = fromPeer;
        this.toPeer = toPeer;
        this.payload = payload;
    }

}

