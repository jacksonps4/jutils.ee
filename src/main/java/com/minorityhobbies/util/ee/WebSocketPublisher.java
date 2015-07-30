package com.minorityhobbies.util.ee;

import javax.websocket.*;
import java.util.HashMap;
import java.util.Map;

public abstract class WebSocketPublisher {
    private static final Map<Class<? extends WebSocketPublisher>, OutboundWebSocketService> WEBSOCKET_PUBLISHERS
            = new HashMap<>();
    private final OutboundWebSocketService outboundWebSocketService =
            WEBSOCKET_PUBLISHERS.computeIfAbsent(getClass(), c -> new OutboundWebSocketService());

    protected abstract boolean filter(String event, Session session);

    public void publish(String event) {
        outboundWebSocketService.publish(event);
    }

    @OnOpen
    public void onOpen(javax.websocket.Session session, EndpointConfig config) {
        outboundWebSocketService.newSession(session, config);
    }

    @OnMessage
    public void onMessage(String event, Session session) {
        if (filter(event, session)) {
            outboundWebSocketService.onMessage(event);
        }
    }

    @OnClose
    public void onClose(javax.websocket.Session session, CloseReason closeReason) {
        outboundWebSocketService.endSession(session, closeReason);
    }
}
