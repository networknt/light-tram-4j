package com.networknt.tram.message.common;

import java.util.Map;
import java.util.Optional;

/**
 * A message
 * @see com.networknt.tram.message.producer.MessageBuilder
 */
public interface Message {
  String ID = "ID";
  String PARTITION_ID = "PARTITION_ID";
  String DESTINATION = "DESTINATION";

  String getId();
  Map<String, String> getHeaders();
  String getPayload();

  Optional<String> getHeader(String name);
  String getRequiredHeader(String name);

  boolean hasHeader(String name);

  void setPayload(String payload);
  void setHeaders(Map<String, String> headers);
  void setHeader(String name, String value);
  void removeHeader(String key);
}
