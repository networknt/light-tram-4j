package com.networknt.tram.command;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.util.Collections.singleton;

public class MyReplyConsumer {

  public final BlockingDeque<Message> messages = new LinkedBlockingDeque<>();

  private ChannelMapping channelMapping = SingletonServiceFactory.getBean(ChannelMapping.class);

  private MessageConsumer messageConsumer;
  private String replyChannel;

  public MyReplyConsumer(MessageConsumer messageConsumer, String replyChannel) {
    this.messageConsumer = messageConsumer;
    this.replyChannel = replyChannel;
    subscribe();
  }

  public void subscribe() {
    messageConsumer.subscribe(getClass().getName(), singleton(channelMapping.transform(replyChannel)), this::handler);
  }

  private void handler(Message message) {
    messages.add(message);
  }

  public String getReplyChannel() {
    return replyChannel;
  }
}
