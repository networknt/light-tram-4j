package com.networknt.tram.command.consumer;


import com.networknt.config.JsonMapper;
import com.networknt.tram.command.common.CommandReplyOutcome;
import com.networknt.tram.command.common.Failure;
import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.common.Success;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageBuilder;

public class CommandHandlerReplyBuilder {


  private static <T> Message with(T reply, CommandReplyOutcome outcome) {
    MessageBuilder messageBuilder = MessageBuilder
            .withPayload(JsonMapper.toJson(reply))
            .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, outcome.name())
            .withHeader(ReplyMessageHeaders.REPLY_TYPE, reply.getClass().getName());
    return messageBuilder.build();
  }

  public static Message withSuccess(Object reply) {
    return with(reply, CommandReplyOutcome.SUCCESS);
  }

  public static Message withSuccess() {
    return withSuccess(new Success());
  }

  public static Message withFailure() {
    return withFailure(new Failure());
  }
  public static Message withFailure(Object reply) {
    return with(reply, CommandReplyOutcome.FAILURE);
  }

}
