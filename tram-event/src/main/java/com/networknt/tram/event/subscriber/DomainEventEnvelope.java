package com.networknt.tram.event.subscriber;


import com.networknt.tram.event.common.DomainEvent;
import com.networknt.tram.message.common.Message;

public interface DomainEventEnvelope<T extends DomainEvent> {
  String getAggregateId();
  Message getMessage();
  String getAggregateType();
  String getEventId();

  T getEvent();
}
