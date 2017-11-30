package com.networknt.tram.command;

import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.tram.command.common.Success;
import com.networknt.tram.command.consumer.CommandHandlers;
import com.networknt.tram.command.consumer.CommandHandlersBuilder;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.command.consumer.PathVariables;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageBuilder;

public class MyTestCommandHandler {


  public CommandHandlers defineCommandHandlers() {
    return CommandHandlersBuilder
            .fromChannel("customerService")
            .resource("/customers/{customerId}")
            .onMessage(MyTestCommand.class, this::myHandlerMethod)
            .build();
  }

  public Message myHandlerMethod(CommandMessage<MyTestCommand> cm, PathVariables pvs) {
    System.out.println("Got command: " + cm);
    return MessageBuilder
            .withPayload(JSonMapper.toJson(new Success()))
            .build();
  }
}
