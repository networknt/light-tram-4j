package com.networknt.tram.command.consumer;

import com.networknt.config.JsonMapper;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.Command;
import com.networknt.tram.command.common.Success;
import com.networknt.tram.command.producer.CommandProducerImpl;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;
import org.junit.Test;

import static java.util.Collections.singletonMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommandDispatcherTest {

  static class CommandDispatcherTestTarget {


    public Message reserveCredit(CommandMessage<TestCommand> cm, PathVariables pathVariables) {

      String customerId = pathVariables.getString("customerId");
      System.out.println("customerId=" + customerId);
      System.out.println("cm=" + cm);
      return MessageBuilder
              .withPayload(JsonMapper.toJson(new Success()))
              .build();

    }

  }

  static class TestCommand implements Command {
    @Override
    public String toString() {
      return JsonMapper.toJson(this);
    }

  }

  public CommandHandlers defineCommandHandlers(CommandDispatcherTestTarget target) {
    return CommandHandlersBuilder
            .fromChannel("customerService")
            .resource("/customers/{customerId}")
            .onMessage(TestCommand.class, target::reserveCredit)
            .build();
  }

  @Test
  @SuppressWarnings("unchecked")
  public void shouldDispatchCommand() {
    String commandDispatcherId = "fooId";

    CommandDispatcherTestTarget target = spy(new CommandDispatcherTestTarget());

    ChannelMapping channelMapping = mock(ChannelMapping.class);

    MessageConsumer messageConsumer = mock(MessageConsumer.class);

    MessageProducer messageProducer = mock(MessageProducer.class);

    CommandDispatcher dispatcher = new CommandDispatcher(commandDispatcherId,
            defineCommandHandlers(target),
            channelMapping,
            messageConsumer,
            messageProducer);

    String customerId = "customer0";
    String resource = "/customers/" + customerId;
    Command command = new TestCommand();

    String replyTo = "replyTo-xxx";

    String channel = "myChannel";

    Message message = CommandProducerImpl.makeMessage(channel, resource, command, replyTo, singletonMap(Message.ID, "999"));

    dispatcher.messageHandler(message);

    verify(target).reserveCredit(any(CommandMessage.class), any(PathVariables.class));
    verify(messageProducer).send(any(), any());
    verifyNoMoreInteractions(messageProducer, target);
  }
}