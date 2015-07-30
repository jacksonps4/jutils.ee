package com.minorityhobbies.util.ee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageProcessor implements Runnable, AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ExecutorService threads;
    private final ConnectionFactory connectionFactory;
    private final MessageListener messageListener;
    private final Destination source;
    private final String sourceName;
    private final int concurrencyLevel;
    private final List<SimpleMessageListener> listeners = new LinkedList<>();

    private Connection connection;

    public MessageProcessor(ConnectionFactory connectionFactory, Destination source, MessageListener messageListener,
                            int concurrencyLevel) {
        this.threads = Executors.newFixedThreadPool(concurrencyLevel);
        this.connectionFactory = connectionFactory;
        this.source = source;
        this.sourceName = null;

        this.messageListener = messageListener;
        this.concurrencyLevel = concurrencyLevel;
    }

    public MessageProcessor(ConnectionFactory connectionFactory, String sourceName, MessageListener messageListener,
                            int concurrencyLevel) {
        this.threads = Executors.newFixedThreadPool(concurrencyLevel);
        this.connectionFactory = connectionFactory;
        this.source = null;
        this.sourceName = sourceName;

        this.messageListener = messageListener;
        this.concurrencyLevel = concurrencyLevel;
    }

    @Override
    public void run() {
        try {
            connection = connectionFactory.createConnection();

            addNewListener();
            connection.start();

            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(1000L);

                double averageIdle = listeners.stream()
                        .mapToInt(SimpleMessageListener::getIdlePercent)
                        .average()
                        .getAsDouble();
                if (logger.isTraceEnabled()) {
                    logger.trace("Load is " + averageIdle);
                }
                if (averageIdle < 15 && listeners.size() < concurrencyLevel) {
                    addNewListener();
                }
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void addNewListener() {
        SimpleMessageListener ml;
        if (source != null) {
            ml = new SimpleMessageListener(connection, source, messageListener);
        } else {
            ml = new SimpleMessageListener(connection, sourceName, messageListener);
        }

        threads.submit(ml);
        listeners.add(ml);
        logger.info("Added new listener. Total = " + listeners.size());
    }

    @Override
    public void close() throws Exception {
        try {
            connection.close();
        } catch (JMSException e) {
            logger.warn("Error closing connection", e);
        }

        threads.shutdownNow();
    }
}
