package com.networknt.tram.producer;

import com.networknt.config.JsonMapper;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.message.common.IdGenerator;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

public class MessageProducerKafkaImpl implements MessageProducer {

    final static private Logger logger = LoggerFactory.getLogger(MessageProducerKafkaImpl.class);

    private IdGenerator idGenerator;
    /**
     * This class should be constructed in service.yml and before MessageProducer binding,
     * we need to have DataSource and IdGenerator bindings defined in service.yml
     */
    public MessageProducerKafkaImpl() {
        this.idGenerator = SingletonServiceFactory.getBean(IdGenerator.class);
    }

    @Override
    public void send(String destination, Message message) {
        Objects.requireNonNull(destination);
        Objects.requireNonNull(message);

        String id = idGenerator.genId().asString();
        message.getHeaders().put(Message.ID, id);

        try (final KafkaProducer kafkaProducer = SingletonServiceFactory.getBean(KafkaProducer.class)) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(destination, id, JsonMapper.toJson(message)));
            kafkaProducer.commitTransaction();

        } catch (Exception e) {
            logger.error("Exception:", e);
            throw new RuntimeException(e);
        }
    }
}
