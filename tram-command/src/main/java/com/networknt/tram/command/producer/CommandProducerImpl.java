package com.networknt.tram.command.producer;


import com.networknt.config.JsonMapper;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.Command;
import com.networknt.tram.command.common.CommandMessageHeaders;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;

import java.util.Map;

public class CommandProducerImpl implements CommandProducer {

  private MessageProducer messageProducer;
  private ChannelMapping channelMapping;

  public CommandProducerImpl(MessageProducer messageProducer, ChannelMapping channelMapping) {
    this.messageProducer = messageProducer;
    this.channelMapping = channelMapping;
  }

  @Override
  public String send(String channel, Command command, String replyTo, Map<String, String> headers) {
    return send(channel, null, command, replyTo, headers);
  }

  @Override
  public String send(String channel, String resource, Command command, String replyTo, Map<String, String> headers) {
    Message message = makeMessage(channel, resource, command, replyTo, headers);
    messageProducer.send(channelMapping.transform(channel), message);
    return message.getId();
  }

  public static Message makeMessage(String channel, String resource, Command command, String replyTo, Map<String, String> headers) {
    MessageBuilder builder = MessageBuilder.withPayload(JsonMapper.toJson(command))
            .withExtraHeaders("", headers) // TODO should these be prefixed??!
            .withHeader(CommandMessageHeaders.DESTINATION, channel)
            .withHeader(CommandMessageHeaders.COMMAND_TYPE, command.getClass().getName())
            .withHeader(CommandMessageHeaders.REPLY_TO, replyTo);

    if (resource != null)
      builder.withHeader(CommandMessageHeaders.RESOURCE, resource);

    return builder
            .build();
  }
}
