package com.networknt.tram.producer;

import com.google.common.collect.Iterables;
import com.networknt.tram.test.KafkaTestBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TramKafkaProducerTest extends KafkaTestBase {
    protected String transactionalId;
    protected Properties extraProperties;

    @Before
    public void before() {
        transactionalId = UUID.randomUUID().toString();
        extraProperties = new Properties();
        extraProperties.putAll(standardProps);
        extraProperties.put("transactional.id", transactionalId);
        extraProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        extraProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        extraProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        extraProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        extraProperties.put("isolation.level", "read_committed");
        System.out.println(extraProperties);
    }

    @Test(timeout = 30000L)
    public void testHappyPath() throws IOException {
        String topicName = "kafka-producer-happy-path";
        try (Producer<String, String> kafkaProducer = new TramKafkaProducer<>(extraProperties)) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(topicName, "42", "42"));
            kafkaProducer.commitTransaction();
        }
        assertRecord(topicName, "42", "42");
        deleteTestTopic(topicName);
    }

    @Test(timeout = 30000L)
    public void testResumeTransaction() throws IOException {
        String topicName = "kafka-producer-resume-transaction";
        try (TramKafkaProducer<String, String> kafkaProducer = new TramKafkaProducer<>(extraProperties)) {
            kafkaProducer.initTransactions();
            kafkaProducer.beginTransaction();
            kafkaProducer.send(new ProducerRecord<>(topicName, "42", "42"));
            kafkaProducer.flush();
            long producerId = kafkaProducer.getProducerId();
            short epoch = kafkaProducer.getEpoch();

            try (TramKafkaProducer<String, String> resumeProducer = new TramKafkaProducer<>(extraProperties)) {
                resumeProducer.resumeTransaction(producerId, epoch);
                resumeProducer.commitTransaction();
            }

            assertRecord(topicName, "42", "42");

            // this shouldn't throw - in case of network split, old producer might attempt to commit it's transaction
            kafkaProducer.commitTransaction();

            // this shouldn't fail also, for same reason as above
            try (TramKafkaProducer<String, String> resumeProducer = new TramKafkaProducer<>(extraProperties)) {
                resumeProducer.resumeTransaction(producerId, epoch);
                resumeProducer.commitTransaction();
            }
        }
        deleteTestTopic(topicName);
    }

    private void assertRecord(String topicName, String expectedKey, String expectedValue) {
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(extraProperties)) {
            kafkaConsumer.subscribe(Collections.singletonList(topicName));
            ConsumerRecords<String, String> records = kafkaConsumer.poll(10000);
            ConsumerRecord<String, String> record = Iterables.getOnlyElement(records);
            assertEquals(expectedKey, record.key());
            assertEquals(expectedValue, record.value());
        }
    }

}

