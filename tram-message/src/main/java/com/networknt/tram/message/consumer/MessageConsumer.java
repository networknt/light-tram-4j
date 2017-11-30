package com.networknt.tram.message.consumer;

import java.util.Set;

public interface MessageConsumer {

  void subscribe(String subscriberId, Set<String> channels, MessageHandler handler);
}
