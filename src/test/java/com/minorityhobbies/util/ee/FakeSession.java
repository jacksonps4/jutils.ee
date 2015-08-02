package com.minorityhobbies.util.ee;

import javax.websocket.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;


public class FakeSession implements Session {
    private final List<String> messages = new CopyOnWriteArrayList<>();
    private boolean open = true;
    private String id = UUID.randomUUID().toString();

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public WebSocketContainer getContainer() {
        return null;
    }

    @Override
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {

    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        return null;
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {

    }

    @Override
    public String getProtocolVersion() {
        return null;
    }

    @Override
    public String getNegotiatedSubprotocol() {
        return null;
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public long getMaxIdleTimeout() {
        return 0;
    }

    @Override
    public void setMaxIdleTimeout(long milliseconds) {

    }

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {

    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        return 0;
    }

    @Override
    public void setMaxTextMessageBufferSize(int length) {

    }

    @Override
    public int getMaxTextMessageBufferSize() {
        return 0;
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        return new RemoteEndpoint.Basic() {
            @Override
            public void sendText(String text) throws IOException {
                messages.add(text);
            }

            @Override
            public void sendBinary(ByteBuffer data) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendText(String partialMessage, boolean isLast) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendBinary(ByteBuffer partialByte, boolean isLast) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public OutputStream getSendStream() throws IOException {
                return null;
            }

            @Override
            public Writer getSendWriter() throws IOException {
                return null;
            }

            @Override
            public void sendObject(Object data) throws IOException, EncodeException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setBatchingAllowed(boolean allowed) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean getBatchingAllowed() {
                return false;
            }

            @Override
            public void flushBatch() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void close() throws IOException {
        open = false;
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        open = false;
    }

    @Override
    public URI getRequestURI() {
        return null;
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public Map<String, String> getPathParameters() {
        return null;
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public Set<Session> getOpenSessions() {
        return null;
    }
}
