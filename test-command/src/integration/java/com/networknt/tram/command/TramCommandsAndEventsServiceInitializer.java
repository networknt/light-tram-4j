package com.networknt.tram.command;

import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.DefaultChannelMapping;
import com.networknt.tram.command.consumer.CommandDispatcher;
import com.networknt.tram.message.consumer.MessageConsumer;

import static org.mockito.Mockito.spy;

public class TramCommandsAndEventsServiceInitializer {

    public ChannelMapping channelMapping() {
        TramCommandsAndEventsIntegrationData data = new TramCommandsAndEventsIntegrationData();
        return DefaultChannelMapping.builder()
                .with("ReplyTo", data.getAggregateDestination())
                .with("customerService", data.getCommandChannel())
                .build();
    }

    public CommandDispatcher consumerCommandDispatcher() {
        MyTestCommandHandler target = spy(new MyTestCommandHandler());
        return new CommandDispatcher("customerCommandDispatcher", target.defineCommandHandlers());
    }

    public MyReplyConsumer myReplyConsumer() {
        MessageConsumer messageConsumer = SingletonServiceFactory.getBean(MessageConsumer.class);
        return new MyReplyConsumer(messageConsumer, "ReplyTo");
    }

    public MyTestCommandHandler myTestCommandHandler() {
        return spy(new MyTestCommandHandler());
    }

}
