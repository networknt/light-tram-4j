package com.networknt.tram.cdc.server;

public class PullingConfig {
    private String tableName;
    private String idField;
    private String publishedField;
    private String headers;
    private String destination;
    private String payload;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getPublishedField() {
        return publishedField;
    }

    public void setPublishedField(String publishedField) {
        this.publishedField = publishedField;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
