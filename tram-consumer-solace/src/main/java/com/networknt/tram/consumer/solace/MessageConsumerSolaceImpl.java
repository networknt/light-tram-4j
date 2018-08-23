package com.networknt.tram.consumer.solace;

import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.solace.consumer.EventuateSolaceConsumer;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.common.MessageImpl;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.consumer.MessageHandler;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class MessageConsumerSolaceImpl implements MessageConsumer {

    private Logger logger = LoggerFactory.getLogger(MessageConsumerSolaceImpl.class);
    private EventuateSolaceConsumer consumer ;

    public MessageConsumerSolaceImpl() {

    }

    @Override
    public void subscribe(String subscriberId, Set<String> channels, MessageHandler handler) {
        BiConsumer<BytesXMLMessage, BiConsumer<Void, Throwable>> solaceHandler = (record, callback) -> {
            Message m = toMessage(record);

            try {
                logger.trace("Invoking handler {} {}", subscriberId, m.getId());
                handler.accept(m);
                logger.trace("handled message {} {}", subscriberId, m.getId());
                callback.accept(null, null);
            } catch (Throwable t) {
                logger.trace("Got exception {} {}", subscriberId, m.getId());
                logger.trace("Got exception ", t);
                callback.accept(null, t);
            }
            //  logger.trace("handled message {} {}", subscriberId, m.getId());
            // callback.accept(null, null);
        };

        EventuateSolaceConsumer kc = new EventuateSolaceConsumer(solaceHandler, new ArrayList<>(channels));
        kc.start();
    }


    @Override
    public void close() {
        consumer.stop();
    }

    private Message toMessage(BytesXMLMessage record) {
        String json;
        if (record instanceof TextMessage) {
            TextMessage txtMessaage = (TextMessage) record;
            json =  txtMessaage.getText();
        } else {
            json = new String(record.getBytes());
        }
        return JSonMapper.fromJson(json, MessageImpl.class);
    }
}
