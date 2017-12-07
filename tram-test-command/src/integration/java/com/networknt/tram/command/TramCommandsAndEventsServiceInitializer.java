package com.networknt.tram.command;

import com.networknt.eventuate.jdbc.EventuateSchema;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.DefaultChannelMapping;
import com.networknt.tram.command.consumer.CommandDispatcher;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageProducer;
import org.mockito.Mockito;

import static org.mockito.Mockito.spy;

public class TramCommandsAndEventsServiceInitializer {

    public EventuateSchema eventuateSchema() {
        return new EventuateSchema();
    }

    public ChannelMapping channelMapping() {
        TramCommandsAndEventsIntegrationData data = new TramCommandsAndEventsIntegrationData();
        return DefaultChannelMapping.builder()
                .with("ReplyTo", data.getAggregateDestination())
                .with("customerService", data.getCommandChannel())
                .build();
    }



    public CommandDispatcher consumerCommandDispatcher() {
        MyTestCommandHandler target = Mockito.spy(new MyTestCommandHandler());
        ChannelMapping channelMapping = SingletonServiceFactory.getBean(ChannelMapping.class);
        MessageConsumer messageConsumer = SingletonServiceFactory.getBean(MessageConsumer.class);
        MessageProducer messageProducer = SingletonServiceFactory.getBean(MessageProducer.class);
        return new CommandDispatcher("customerCommandDispatcher",
                target.defineCommandHandlers(), channelMapping, messageConsumer, messageProducer);
    }

    public MyReplyConsumer myReplyConsumer() {
        MessageConsumer messageConsumer = SingletonServiceFactory.getBean(MessageConsumer.class);
        return new MyReplyConsumer(messageConsumer, "ReplyTo");
    }

    public MyTestCommandHandler myTestCommandHandler() {
        return spy(new MyTestCommandHandler());
    }

}
