package com.minorityhobbies.util.ee;

import javax.websocket.Session;

public class NoFilterWebSocketPublisher extends WebSocketPublisher {
    @Override
    protected boolean filter(String event, Session session) {
        return true;
    }
}
