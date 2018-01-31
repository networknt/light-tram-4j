package com.networknt.tram.cdc.polling.connector;

import com.networknt.eventuate.cdc.polling.PollingDataProvider;
import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.jdbc.EventuateSchema;
import com.networknt.tram.cdc.mysql.connector.MessageWithDestination;
import com.networknt.tram.message.common.MessageImpl;

import java.util.Map;


public class MessagePollingDataProvider implements PollingDataProvider<PublishedMessageBean, MessageWithDestination, String> {

  private EventuateSchema eventuateSchema;
  private String table;
  private String publishedField = "PUBLISHED";
  private String idField = "ID";
  private String headers = "HEADERS";
  private String destination = "DESTINATION";
  private String payload = "PAYLOAD";

  public MessagePollingDataProvider() {
    this(new EventuateSchema());
  }

  public MessagePollingDataProvider(EventuateSchema eventuateSchema) {
    this.eventuateSchema = eventuateSchema;
    table = this.eventuateSchema.qualifyTable("MESSAGE");
  }

  @Override
  public String table() {
    return table;
  }

  @Override
  public Class<PublishedMessageBean> eventBeanClass() {
    return PublishedMessageBean.class;
  }

  @Override
  public String getId(MessageWithDestination data) {
    return data.getMessage().getId();
  }

  @Override
  public String publishedField() {
    return this.publishedField;
  }

  @Override
  public String idField() {
    return this.idField;
  }

  public String headersField() {
    return this.headers;
  }

  public String payloadField() {
    return this.payload;
  }

  public String destinationField() {
    return this.destination;
  }

  @Override
  public MessageWithDestination transformEventBeanToEvent(PublishedMessageBean eventBean) {
    String payload =eventBean.getPayload();
    Map<String, String> headers = JSonMapper.fromJson(eventBean.getHeaders(), Map.class);
    return new MessageWithDestination(eventBean.getDestination(),new MessageImpl(payload, headers), null);
  }

  public void reset(String table, String idField, String publishedField, String headers, String destination, String payload) {
    this.table = eventuateSchema.qualifyTable(table);
    this.idField = idField;
    this.publishedField = publishedField;
    this.headers = headers;
    this.destination = destination;
    this.payload = payload;
  }
}
