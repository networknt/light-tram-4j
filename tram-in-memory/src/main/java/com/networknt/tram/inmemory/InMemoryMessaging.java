package com.networknt.tram.inmemory;


import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.consumer.MessageHandler;
import com.networknt.tram.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InMemoryMessaging implements MessageProducer, MessageConsumer {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private IdGenerator idGenerator = SingletonServiceFactory.getBean(IdGenerator.class);

  private Executor executor = Executors.newCachedThreadPool();

  @Override
  public void send(String destination, Message message) {
    String id = idGenerator.genId().asString();
    message.getHeaders().put(Message.ID, id);
    reallySend(destination, message);
  }

  private void reallySend(String destination, Message message) {
    List<MessageHandler> handlers = subscriptions.getOrDefault(destination, Collections.emptyList());
    logger.info("sending to channel {} that has {} subscriptions this message {} ", destination, handlers.size(), message);
    for (MessageHandler handler : handlers) {
      try {
        handler.accept(message);
      } catch (Throwable t) {
        logger.error("message handler " + destination, t);
      }
    }
  }

  private Map<String, List<MessageHandler>> subscriptions = new HashMap<>();

  @Override
  public void subscribe(String subscriberId, Set<String> channels, MessageHandler handler) {
    logger.info("subscribing {} to channels {}", subscriberId, channels);
    for (String channel : channels) {
      List<MessageHandler> handlers = subscriptions.get(channel);
      if (handlers == null) {
        handlers = new ArrayList<>();
        subscriptions.put(channel, handlers);
      }
      handlers.add(handler);
    }
  }
}
