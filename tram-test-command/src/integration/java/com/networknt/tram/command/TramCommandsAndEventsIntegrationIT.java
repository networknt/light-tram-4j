package com.networknt.tram.command;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.DefaultChannelMapping;
import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.consumer.CommandDispatcher;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.command.consumer.PathVariables;
import com.networknt.tram.command.producer.CommandProducer;
import com.networknt.tram.command.producer.CommandProducerImpl;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageProducer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class TramCommandsAndEventsIntegrationIT {
  static final private Logger logger = LoggerFactory.getLogger(TramCommandsAndEventsIntegrationIT.class);
  private CommandProducer commandProducer;

  private MyReplyConsumer myReplyConsumer;

  private MyTestCommandHandler myTestCommandHandler;

  @Before
  public void setUp() {
    myReplyConsumer = SingletonServiceFactory.getBean(MyReplyConsumer.class);
    logger.debug("myReplyConsumer = " + myReplyConsumer);
    commandProducer = SingletonServiceFactory.getBean(CommandProducer.class);
    logger.debug("commandProducer = " + commandProducer);
    myTestCommandHandler = SingletonServiceFactory.getBean(MyTestCommandHandler.class);
    logger.debug("myTestCommandHandler = " + myTestCommandHandler);
  }

  //@Test
  public void shouldDoSomething() throws InterruptedException {
    String messageId = commandProducer.send("customerService", "/customers/10",
            new MyTestCommand(), myReplyConsumer.getReplyChannel(),
            Collections.emptyMap());

    Message m = myReplyConsumer.messages.poll(30, TimeUnit.SECONDS);

    assertNotNull(m);

    assertEquals(messageId, m.getRequiredHeader(ReplyMessageHeaders.IN_REPLY_TO));

    System.out.println("Received m=" + m);
  }
}
