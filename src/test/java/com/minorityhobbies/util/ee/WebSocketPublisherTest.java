package com.minorityhobbies.util.ee;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class WebSocketPublisherTest {
    private WebSocketPublisher noFilterWebSocketPublisher1;
    private WebSocketPublisher noFilterWebSocketPublisher2;
    private FakeSession session1;
    private FakeSession session2;

    @Before
    public void setUp() {
        session1 = new FakeSession();
        session2 = new FakeSession();

        noFilterWebSocketPublisher1 = new WebSocketPublisher();
        noFilterWebSocketPublisher2 = new WebSocketPublisher();

        noFilterWebSocketPublisher1.onOpen(session1, null);
        noFilterWebSocketPublisher2.onOpen(session2, null);
    }

    @Test
    public void singleMessageReceivedByBothSessions() {
        noFilterWebSocketPublisher1.publish("test1");
        assertEquals(1, session1.getMessages().size());
        assertEquals(1, session2.getMessages().size());
    }

    @Test
    public void twoMessagesReceived() {
        singleMessageReceivedByBothSessions();
        noFilterWebSocketPublisher2.publish("test2");

        assertEquals(2, session1.getMessages().size());
        assertEquals(2, session2.getMessages().size());
    }

    @Test
    public void twoMessagesAfterSessionClosed() throws IOException {
        singleMessageReceivedByBothSessions();
        session1.close();

        noFilterWebSocketPublisher2.publish("test2");

        assertEquals(1, session1.getMessages().size());
        assertEquals(2, session2.getMessages().size());
    }

    @Test
    public void twoMessagesAfterSessionClosedAndReopened() throws IOException {
        singleMessageReceivedByBothSessions();

        session1.close();
        session1 = new FakeSession();
        noFilterWebSocketPublisher1.onOpen(session1, null);

        noFilterWebSocketPublisher2.publish("test2");

        assertEquals(1, session1.getMessages().size());
        assertEquals(2, session2.getMessages().size());
    }
}
