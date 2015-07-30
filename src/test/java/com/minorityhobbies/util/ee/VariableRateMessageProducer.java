package com.minorityhobbies.util.ee;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class VariableRateMessageProducer implements Runnable, AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConnectionFactory connectionFactory;
    private final String targetName;
    private volatile int ratePerSecond = 100;
    private Connection connection;
    private Session session;
    private Destination destination;

    public VariableRateMessageProducer(ConnectionFactory connectionFactory, String targetName) {
        this.connectionFactory = connectionFactory;
        this.targetName = targetName;
    }

    @Override
    public void run() {
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createTopic(targetName);
        } catch (JMSException e) {
            logger.error("Failed to create connection / session", e);
            throw new RuntimeException(e);
        }

        try {
            TextMessage msg = session.createTextMessage(generateRandomText());
            MessageProducer producer = session.createProducer(destination);

            while (!Thread.currentThread().isInterrupted()) {
                producer.send(msg);

                int sleepTimeNanos = calculateSleepTime();
                long sleepTimeMillis = TimeUnit.MILLISECONDS.convert(sleepTimeNanos, TimeUnit.NANOSECONDS);
                int remainingNanos = sleepTimeNanos / 1000000;
                if (sleepTimeMillis > 0) {
                    Thread.sleep(sleepTimeMillis, remainingNanos);
                } else {
                    Thread.sleep(0, sleepTimeNanos);
                }
            }
        } catch (JMSException e) {
            logger.error("Failed to create / send message", e);
        } catch (InterruptedException e) {
            logger.error("Stopped", e);
        }
    }

    private int calculateSleepTime() {
        return 1000000000 / ratePerSecond;
    }

    private String generateRandomText() {
        return null;
    }

    public int getRatePerSecond() {
        return ratePerSecond;
    }

    public void setRatePerSecond(int ratePerSecond) {
        this.ratePerSecond = ratePerSecond;
    }

    @Override
    public void close() throws Exception {
        try {
            session.close();
        } catch (JMSException e) {

        }
        try {
            connection.close();
        } catch (JMSException e) {

        }
    }

    public static void main(String[] args) throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        ConnectionFactory cf = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");

        final Logger logger = LoggerFactory.getLogger("MessageProducerHarness");
        MessageProcessor messageProcessor = new MessageProcessor(cf, "test.topic", new MessageListener() {
            private final AtomicLong received = new AtomicLong();

            @Override
            public void onMessage(Message message) {
                long receivedCount = received.incrementAndGet();
                if (receivedCount % 1000 == 0) {
                    logger.info(String.format("Received %n message", receivedCount));
                }
            }
        }, 10);
        executorService.submit(messageProcessor);

        VariableRateMessageProducer mp = new VariableRateMessageProducer(cf, "test.topic");
        executorService.submit(mp);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (!Thread.currentThread().isInterrupted()) {
            System.out.print("Enter rate: ");
            String line = reader.readLine();
            mp.setRatePerSecond(Integer.parseInt(line));
            System.out.println();

            System.out.println("Rate is " + mp.getRatePerSecond());
        }

        executorService.shutdownNow();
    }
}
