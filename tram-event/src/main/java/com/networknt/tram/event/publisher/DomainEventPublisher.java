package com.networknt.tram.event.publisher;

import com.networknt.tram.event.common.DomainEvent;

import java.util.List;
import java.util.Map;

public interface DomainEventPublisher {

  void publish(String aggregateType, Object aggregateId, List<DomainEvent> domainEvents);
  void publish(String aggregateType, Object aggregateId, Map<String, String> headers, List<DomainEvent> domainEvents);

  void publish(String aggregateType, Object aggregateId, Map<String, Object> messageMap);
  void publish(String aggregateType, Object aggregateId, Map<String, String> headers, Map<String, Object> messageMap);

  default void publish(Class<?> aggregateType, Object aggregateId, List<DomainEvent> domainEvents) {
    publish(aggregateType.getName(), aggregateId, domainEvents);
  }
}
