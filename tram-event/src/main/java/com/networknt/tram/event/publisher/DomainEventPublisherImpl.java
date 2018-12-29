package com.networknt.tram.event.publisher;

import com.networknt.config.JsonMapper;
import com.networknt.tram.event.common.DomainEvent;
import com.networknt.tram.event.common.EventMessageHeaders;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DomainEventPublisherImpl implements DomainEventPublisher {

  private MessageProducer messageProducer;

  public DomainEventPublisherImpl(MessageProducer messageProducer) {
    this.messageProducer = messageProducer;
  }

  @Override
  public void publish(String aggregateType, Object aggregateId, List<DomainEvent> domainEvents) {
    publish(aggregateType, aggregateId, Collections.emptyMap(), domainEvents);
  }

  @Override
  public void publish(String aggregateType, Object aggregateId, Map<String, String> headers, List<DomainEvent> domainEvents) {
    for (DomainEvent event : domainEvents) {
      messageProducer.send(aggregateType,
              makeMessageForDomainEvent(aggregateType, aggregateId, headers, event));

    }
  }

  public static Message makeMessageForDomainEvent(String aggregateType, Object aggregateId, Map<String, String> headers, DomainEvent event) {
    String aggregateIdAsString = aggregateId.toString();
    return MessageBuilder
            .withPayload(JsonMapper.toJson(event))
            .withExtraHeaders("", headers)
            .withHeader(Message.PARTITION_ID, aggregateIdAsString)
            .withHeader(EventMessageHeaders.AGGREGATE_ID, aggregateIdAsString)
            .withHeader(EventMessageHeaders.AGGREGATE_TYPE, aggregateType)
            .withHeader(EventMessageHeaders.EVENT_TYPE, event.getClass().getName())
            .build();
  }
}
