package com.networknt.tram.test.mysqlkafka;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.consumer.MessageHandler;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class TramIntegrationIT {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private MessageProducer messageProducer = SingletonServiceFactory.getBean(MessageProducer.class);

  private MessageConsumer messageConsumer = SingletonServiceFactory.getBean(MessageConsumer.class);

  @Test
  public void shouldDoSomething() throws InterruptedException {
    String destination = "Destination-" + System.currentTimeMillis();
    String subscriberId = "SubscriberId-" + System.currentTimeMillis();

    CountDownLatch latch = new CountDownLatch(1);

    MessageHandler handler = message -> {
      System.out.println("Got message=" + message);
      latch.countDown();
    };

    messageConsumer.subscribe(subscriberId, Collections.singleton(destination), handler);

    messageProducer.send(destination, MessageBuilder.withPayload("Hello").build());

    assertTrue("Expected message", latch.await(10, TimeUnit.SECONDS));

  }
}
