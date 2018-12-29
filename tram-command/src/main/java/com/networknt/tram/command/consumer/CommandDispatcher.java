package com.networknt.tram.command.consumer;

import com.networknt.config.JsonMapper;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.CommandMessageHeaders;
import com.networknt.tram.command.common.Failure;
import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.common.paths.ResourcePath;
import com.networknt.tram.command.common.paths.ResourcePathPattern;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_MAP;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toSet;

public class CommandDispatcher {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private String commandDispatcherId;
  private CommandHandlers commandHandlers;

  private ChannelMapping channelMapping;

  private MessageConsumer messageConsumer;

  private MessageProducer messageProducer;

  public CommandDispatcher(String commandDispatcherId,
                           CommandHandlers commandHandlers,
                           ChannelMapping channelMapping,
                           MessageConsumer messageConsumer,
                           MessageProducer messageProducer) {
    this.commandDispatcherId = commandDispatcherId;
    this.commandHandlers = commandHandlers;
    this.channelMapping = channelMapping;
    this.messageConsumer = messageConsumer;
    this.messageProducer = messageProducer;
    initialize();
  }

  @PostConstruct
  public void initialize() {
    messageConsumer.subscribe(commandDispatcherId,
            commandHandlers.getChannels().stream().map(channelMapping::transform).collect(toSet()),
            this::messageHandler);
  }

  public void messageHandler(Message message) {
    logger.trace("Received message {} {}", commandDispatcherId, message);

    Optional<CommandHandler> possibleMethod = commandHandlers.findTargetMethod(message);
    if (!possibleMethod.isPresent()) {
      throw new RuntimeException("No method for " + message);
    }

    CommandHandler m = possibleMethod.get();

    Object param = convertPayload(m, message.getPayload());

    Map<String, String> correlationHeaders = correlationHeaders(message.getHeaders());

    Map<String, String> pathVars = getPathVars(message, m);



    Optional<String> defaultReplyChannel = message.getHeader(CommandMessageHeaders.REPLY_TO);

    List<Message> replies;
    try {
      CommandMessage cm = new CommandMessage(message.getId(), param, correlationHeaders, message);
      replies = invoke(m, cm, pathVars);
      logger.trace("Generated replies {} {} {}", commandDispatcherId, message, replies);
    } catch (Exception e) {
      logger.trace("Generated error {} {} {}", commandDispatcherId, message, e.getClass().getName());
      handleException(message, param, m, e, pathVars, defaultReplyChannel);
      return;
    }

    if (replies != null) {
      publish(correlationHeaders, replies, defaultReplyChannel);
    } else {
      logger.trace("Null replies - not publishling");
    }
  }

  protected List<Message> invoke(CommandHandler commandHandler, CommandMessage cm, Map<String, String> pathVars) {
    return commandHandler.invokeMethod(cm, pathVars);
  }

  protected Object convertPayload(CommandHandler m, String payload) {
    Class<?> paramType = findCommandParameterType(m);
    return JsonMapper.fromJson(payload, paramType);
  }

  private Map<String, String> getPathVars(Message message, CommandHandler handler) {
    return handler.getResource().flatMap( res -> {
      ResourcePathPattern r = ResourcePathPattern.parse(res);
      return message.getHeader(CommandMessageHeaders.RESOURCE).map(h -> {
        ResourcePath mr = ResourcePath.parse(h);
        return r.getPathVariableValues(mr);
      });
    }).orElse(EMPTY_MAP);
  }

  private void publish(Map<String, String> correlationHeaders, List<Message> replies, Optional<String> defaultReplyChannel) {
    for (Message reply : replies)
      messageProducer.send(channelMapping.transform(destination(defaultReplyChannel)),
              MessageBuilder
                      .withMessage(reply)
                      .withExtraHeaders("", correlationHeaders)
                      .build());
  }

  private String destination(Optional<String> defaultReplyChannel) {
    return defaultReplyChannel.orElseGet(() -> {
      throw new RuntimeException();
    });
  }

  private Map<String, String> correlationHeaders(Map<String, String> headers) {
    Map<String, String> m = headers.entrySet()
            .stream()
            .filter(e -> e.getKey().startsWith(CommandMessageHeaders.COMMAND_HEADER_PREFIX))
            .collect(Collectors.toMap(e -> CommandMessageHeaders.inReply(e.getKey()),
                    Map.Entry::getValue));
    m.put(ReplyMessageHeaders.IN_REPLY_TO, headers.get(Message.ID));
    return m;
  }

  private void handleException(Message message, Object param,
                               CommandHandler commandHandler,
                               Throwable cause,
                               Map<String, String> pathVars,
                               Optional<String> defaultReplyChannel) {
    Optional<CommandExceptionHandler> m = commandHandlers.findExceptionHandler(cause);

    logger.info("Handler for {} is {}", cause.getClass(), m);


    if (m.isPresent()) {
      List<Message> replies = m.get().invoke(cause);
      publish(correlationHeaders(message.getHeaders()), replies, defaultReplyChannel);
    } else {
      List<Message> replies = singletonList(MessageBuilder.withPayload(JsonMapper.toJson(new Failure())).build());
      publish(correlationHeaders(message.getHeaders()), replies, defaultReplyChannel);
    }
  }


  private Class findCommandParameterType(CommandHandler m) {
    return m.getCommandClass();
  }

  public void finish() {
    messageConsumer.close();
  }
}
