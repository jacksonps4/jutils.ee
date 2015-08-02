package com.minorityhobbies.util.ee;

import javax.websocket.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A broadcast websocket endpoint to which clients can connect to receive messages.
 * By default in this implementation, all messages that are published using the publish method are
 * broadcast to every websocket session. Subclasses can decide which messages are published by
 * overriding the filter method.
 */
public class WebSocketPublisher {
    private static final Map<Class<? extends WebSocketPublisher>, OutboundWebSocketService> WEBSOCKET_PUBLISHERS
            = new HashMap<>();
    private final OutboundWebSocketService outboundWebSocketService =
            WEBSOCKET_PUBLISHERS.computeIfAbsent(getClass(), c -> new OutboundWebSocketService());

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
     * @param config    The endpoint config
     */
    protected void sessionClosed(javax.websocket.Session session, CloseReason closeReason) { }
}
