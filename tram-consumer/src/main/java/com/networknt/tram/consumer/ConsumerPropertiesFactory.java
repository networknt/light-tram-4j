package com.networknt.tram.consumer;

import java.util.Properties;

public class ConsumerPropertiesFactory {
  public static Properties makeConsumerProperties(KafkaConsumerConfig config, String subscriberId) {
    Properties consumerProperties = new Properties();
    consumerProperties.put("bootstrap.servers", config.getBootstrapServers());
    consumerProperties.put("group.id", subscriberId);
    consumerProperties.put("enable.auto.commit", config.isEnableAutoCommit());
    consumerProperties.put("key.deserializer", config.getKeyDeserializer());
    consumerProperties.put("value.deserializer", config.getValueDeserializer());
    consumerProperties.put("isolation.level", config.getIsolationLevel());
    return consumerProperties;
  }
}
