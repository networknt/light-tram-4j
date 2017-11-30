package com.networknt.tram.command;

import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.command.consumer.PathVariables;
import com.networknt.tram.command.producer.CommandProducer;
import com.networknt.tram.message.common.Message;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class TramCommandsAndEventsIntegrationTest {

  private CommandProducer commandProducer;

  private MyReplyConsumer myReplyConsumer;

  private MyTestCommandHandler myTestCommandHandler;

  @Test
  public void shouldDoSomething() throws InterruptedException {
    String messageId = commandProducer.send("customerService", "/customers/10",
            new MyTestCommand(), myReplyConsumer.getReplyChannel(),
            Collections.emptyMap());

    Message m = myReplyConsumer.messages.poll(30, TimeUnit.SECONDS);

    assertNotNull(m);

    assertEquals(messageId, m.getRequiredHeader(ReplyMessageHeaders.IN_REPLY_TO));

    System.out.println("Received m=" + m);

    verify(myTestCommandHandler).myHandlerMethod(any(CommandMessage.class), any(PathVariables.class));
  }
}
