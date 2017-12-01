package com.networknt.tram.testutil;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.message.consumer.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class TestMessageConsumerFactory {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private MessageConsumer messageConsumer = SingletonServiceFactory.getBean(MessageConsumer.class);


  public TestMessageConsumer make() {
    String replyChannel = "reply-channel-" + System.currentTimeMillis();
    String subscriberId = "subscriberId-" + System.currentTimeMillis();

    TestMessageConsumer consumer = new TestMessageConsumer(replyChannel);

    messageConsumer.subscribe(subscriberId, Collections.singleton(replyChannel), consumer);

    return consumer;

  }
}

