package com.networknt.tram.consumer.kafka;

public interface DuplicateMessageDetector {

  boolean isDuplicate(String consumerId, String messageId);
}
