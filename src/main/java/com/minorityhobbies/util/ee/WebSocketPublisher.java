package com.minorityhobbies.util.ee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A broadcast websocket endpoint to which clients can connect to receive messages.
 * By default in this implementation, all messages that are published using the publish method are
 * broadcast to every websocket session. Subclasses can decide which messages are published by
 * overriding the filter method.
 */
public class WebSocketPublisher {
    private static final Map<Class<? extends WebSocketPublisher>, OutboundWebSocketService> WEBSOCKET_PUBLISHERS
            = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OutboundWebSocketService outboundWebSocketService;

    public WebSocketPublisher() {
        this.outboundWebSocketService = WEBSOCKET_PUBLISHERS
                .computeIfAbsent(getClass(), k -> init(k));
    }

    private OutboundWebSocketService init(Class<?> clazz) {
        logger.debug(String.format("Creating new websocket publisher for class %s", clazz));
        return new OutboundWebSocketService();
    }

    /**
     * Inspects an event and returns true if the event should be published to all socket endpoints.
     * @param event     The event.
     * @param session   The session to which this message is being sent.
     * @return  true if the event should be published, false otherwise.
     */
    protected boolean filter(String event, Session session) {
        return true;
    }

    /**
     * Sends the specified event to all connected sessions.
     *
     * @param event The event.
     */
    public void publish(String event) {
        outboundWebSocketService.publish(event);
    }

    @OnOpen
    public final void onOpen(javax.websocket.Session session, EndpointConfig config) {
        logger.debug(String.format("Opening websocket session %s", session.getId()));

        outboundWebSocketService.newSession(session, config);
        sessionOpened(session, config);
    }

    /**
     * Allows subclasses to hook into the open event. Subclasses wishing to do this should
     * override this method.
     *
     * This is invoked when a websocket session is opened.
     *
     * @param session   The websocket session
     * @param config    The endpoint config
     */
    protected void sessionOpened(javax.websocket.Session session, EndpointConfig config) {}

    @OnMessage
    public final void onMessage(String event, Session session) {
        if (filter(event, session)) {
            outboundWebSocketService.onMessage(event);
        }
    }

    @OnClose
    public final void onClose(javax.websocket.Session session, CloseReason closeReason) {
        logger.debug(String.format("Closing websocket session %s for reason %s", session.getId(), closeReason.toString()));

        outboundWebSocketService.endSession(session, closeReason);
        sessionClosed(session, closeReason);
    }

    /**
     * Allows subclasses to hook into the close event. Subclasses wishing to do this should
     * override this method.
     *
     * This is invoked when a websocket session is closed.
     *
     * @param session   The websocket session
     * @param closeReason  The reason for closure
     */
    protected void sessionClosed(javax.websocket.Session session, CloseReason closeReason) { }
}
