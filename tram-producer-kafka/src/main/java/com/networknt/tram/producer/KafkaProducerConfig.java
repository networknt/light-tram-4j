package com.networknt.tram.producer;

public class KafkaProducerConfig {
    public static final String CONFIG_NAME = "kafka-producer";

    private String acks;
    private int retries;
    private int batchSize;
    private int lingerMs;
    private long bufferMemory;
    private String keySerializer;
    private String valueSerializer;
    private boolean enableAutoCommit;
    private int sessionTimeout;
    private String autoOffsetreset;
    private String bootstrapServers;
    private String keyDeSerializer;
    private String valueDeSerializer;
    private String transactionId;
    private int transactionTimeoutMs;
    private int transactionalIdExpirationMs;

    public KafkaProducerConfig() {
    }

    public String getAcks() {
        return acks;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public long getBufferMemory() {
        return bufferMemory;
    }

    public void setBufferMemory(long bufferMemory) {
        this.bufferMemory = bufferMemory;
    }

    public String getKeySerializer() {
        return keySerializer;
    }

    public void setKeySerializer(String keySerializer) {
        this.keySerializer = keySerializer;
    }

    public String getValueSerializer() {
        return valueSerializer;
    }

    public void setValueSerializer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public boolean isEnableAutoCommit() {
        return enableAutoCommit;
    }

    public void setEnableAutoCommit(boolean enableAutoCommit) {
        this.enableAutoCommit = enableAutoCommit;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getAutoOffsetreset() {
        return autoOffsetreset;
    }

    public void setAutoOffsetreset(String autoOffsetreset) {
        this.autoOffsetreset = autoOffsetreset;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getKeyDeSerializer() {
        return keyDeSerializer;
    }

    public void setKeyDeSerializer(String keyDeSerializer) {
        this.keyDeSerializer = keyDeSerializer;
    }

    public String getValueDeSerializer() {
        return valueDeSerializer;
    }

    public void setValueDeSerializer(String valueDeSerializer) {
        this.valueDeSerializer = valueDeSerializer;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getTransactionTimeoutMs() {
        return transactionTimeoutMs;
    }

    public void setTransactionTimeoutMs(int transactionTimeoutMs) {
        this.transactionTimeoutMs = transactionTimeoutMs;
    }

    public int getTransactionalIdExpirationMs() {
        return transactionalIdExpirationMs;
    }

    public void setTransactionalIdExpirationMs(int transactionalIdExpirationMs) {
        this.transactionalIdExpirationMs = transactionalIdExpirationMs;
    }
}
