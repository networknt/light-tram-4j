package com.networknt.tram.cdc.polling.connector;


public class PublishedMessageBean {
  private String id;
  private String destination;
  private String headers;
  private String payload;

  public PublishedMessageBean() {
  }

  public PublishedMessageBean(String id,
                              String destination,
                              String headers,
                              String payload) {

    this.id = id;
    this.destination = destination;
    this.headers = headers;
    this.payload = payload;

  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public String getHeaders() {
    return headers;
  }

  public void setHeaders(String headers) {
    this.headers = headers;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }
}
