package com.networknt.tram.message.producer;

import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.common.MessageInterceptor;

import java.util.Arrays;

public abstract class AbstractMessageProducer implements  MessageProducer {

  protected MessageInterceptor[] messageInterceptors;

  protected AbstractMessageProducer(MessageInterceptor[] messageInterceptors) {
    this.messageInterceptors = messageInterceptors;
  }


  protected void preSend(Message message) {
    Arrays.stream(messageInterceptors).forEach(mi -> mi.preSend(message));
  }


  protected void postSend(Message message, RuntimeException e) {
    Arrays.stream(messageInterceptors).forEach(mi -> mi.postSend(message, e));
  }

  protected void sendMessage(String destination, Message message) {
    message.getHeaders().put(Message.DESTINATION, destination);
    preSend(message);
    try {
      reallySendMessage(message);
      postSend(message, null);
    } catch (RuntimeException e) {
      postSend(message, e);
      throw e;
    }
  }

  protected abstract void reallySendMessage(Message message);
}
