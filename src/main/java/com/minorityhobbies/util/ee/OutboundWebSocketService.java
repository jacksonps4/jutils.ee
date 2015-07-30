package com.minorityhobbies.util.ee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OutboundWebSocketService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Session> websocketSessions = new CopyOnWriteArrayList<>();

    public void newSession(Session session, EndpointConfig config) {
        websocketSessions.add(session);
    }

    public void onMessage(String event) {
        // noop
    }

    public void endSession(Session session, CloseReason closeReason) {
        websocketSessions.remove(session);
    }

    public void publish(String event) {
        websocketSessions.stream()
                .filter(Session::isOpen)
                .forEach(s -> publish(s, event));
    }

    private void publish(Session session, String event) {
        try {
            session.getBasicRemote().sendText(event);
        } catch (IOException e) {
            logger.error("Failed to write to websocket", e);
        }
    }
}
