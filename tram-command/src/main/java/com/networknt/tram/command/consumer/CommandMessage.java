package com.networknt.tram.command.consumer;


import com.networknt.config.JsonMapper;
import com.networknt.tram.message.common.Message;

import java.util.Map;

public class CommandMessage<T> {

  private String messageId;
  private T command;
  private Map<String, String> correlationHeaders;
  private Message message;

  public Message getMessage() {
    return message;
  }

  public CommandMessage(String messageId, T command, Map<String, String> correlationHeaders, Message message) {
    this.messageId = messageId;
    this.command = command;
    this.correlationHeaders = correlationHeaders;
    this.message = message;
  }

  @Override
  public String toString() {
    return JsonMapper.toJson(this);
  }

  public String getMessageId() {
    return messageId;
  }

  public T getCommand() {
    return command;
  }

  public Map<String, String> getCorrelationHeaders() {
    return correlationHeaders;
  }
}
