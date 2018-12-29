package com.networknt.tram.consumer;

import com.networknt.config.JsonMapper;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.common.MessageImpl;
import com.networknt.tram.message.common.MessageInterceptor;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.consumer.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class MessageConsumerKafkaImpl implements MessageConsumer {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private String bootstrapServers;
  private List<TramKafkaConsumer> consumers = new ArrayList<>();

  public MessageConsumerKafkaImpl(String bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @Override
  public void subscribe(String subscriberId, Set<String> channels, MessageHandler handler) {
    SwimlaneBasedDispatcher swimlaneBasedDispatcher = new SwimlaneBasedDispatcher(subscriberId, Executors.newCachedThreadPool());

    BiConsumer<ConsumerRecord<String, String>, BiConsumer<Void, Throwable>> kcHandler = (record, callback) -> {
      swimlaneBasedDispatcher.dispatch(toMessage(record), record.partition(), message -> {
                try {
                  logger.trace("Invoking handler {} {}", subscriberId, message.getId());
                  handler.accept(message);
                } catch (Throwable t) {
                  logger.trace("Got exception {} {}", subscriberId, message.getId());
                  logger.trace("Got exception ", t);
                  callback.accept(null, t);
                }
                logger.trace("handled message {} {}", subscriberId, message.getId());
                callback.accept(null, null);
              }
      );
    };

    TramKafkaConsumer kc = new TramKafkaConsumer(subscriberId,
            kcHandler,
            new ArrayList<>(channels)
            );

    consumers.add(kc);

    kc.start();
  }

  public void close() {
    consumers.forEach(TramKafkaConsumer::stop);
  }

  private Message toMessage(ConsumerRecord<String, String> record) {
    return JsonMapper.fromJson(record.value(), MessageImpl.class);
  }
}
