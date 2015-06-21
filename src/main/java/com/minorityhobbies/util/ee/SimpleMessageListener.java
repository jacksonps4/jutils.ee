package com.minorityhobbies.util.ee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

class SimpleMessageListener implements Runnable, Closeable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Connection connection;
    private final Destination source;
    private final MessageListener messageListener;

    private final AtomicLong idleNanos = new AtomicLong();
    private final long startTime = System.nanoTime();
    private boolean idle = false;
    private long lastIdleTime;

    private Session session;
    private MessageConsumer consumer;

    public SimpleMessageListener(Connection connection, Destination source, MessageListener messageListener) {
        this.connection = connection;
        this.source = source;
        this.messageListener = messageListener;
    }

    @Override
    public void run() {
        try {
            session = connection.createSession();
            consumer = session.createConsumer(source);
        } catch (JMSException e) {
            logger.error("Failed to create consumer", e);
        }

        try {
            while (!Thread.currentThread().isInterrupted()) {
                Message msg = consumer.receiveNoWait();
                if (msg != null) {
                    working();
                    messageListener.onMessage(msg);
                } else {
                    idle();
                }
            }
        } catch (JMSException e) {
            logger.error("Failed to create consumer", e);
        } catch (Exception e) {
            logger.error("Unexpected item in bagging area", e);
        }
    }

    void idle() {
        long time = System.nanoTime();
        if (this.idle) {
            idleNanos.addAndGet(time - lastIdleTime);
        }

        this.lastIdleTime = time;
        this.idle = true;

        Thread.yield();
    }

    void working() {
        this.idle = false;
    }

    public int getIdlePercent() {
        long totalTime = System.nanoTime() - startTime;
        return (int) (this.idleNanos.get() / totalTime);
    }

    @Override
    public void close() throws IOException {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (JMSException e) {
                logger.warn("Error closing consumer", e);
            }
        }
    }
}
