package com.networknt.tram.cdc.polling.connector;

import com.networknt.eventuate.cdc.polling.PollingDataProvider;
import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.jdbc.EventuateSchema;
import com.networknt.tram.cdc.mysql.connector.MessageWithDestination;
import com.networknt.tram.message.common.MessageImpl;

import java.util.Map;


public class MessagePollingDataProvider implements PollingDataProvider<PublishedMessageBean, MessageWithDestination, String> {

  private String table;

  public MessagePollingDataProvider() {
    this(new EventuateSchema());
  }

  public MessagePollingDataProvider(EventuateSchema eventuateSchema) {
    table = eventuateSchema.qualifyTable("MESSAGE");
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
    return "PUBLISHED";
  }

  @Override
  public String idField() {
    return "ID";
  }

  @Override
  public MessageWithDestination transformEventBeanToEvent(PublishedMessageBean eventBean) {
    String payload =eventBean.getPayload();
    Map<String, String> headers = JSonMapper.fromJson(eventBean.getHeaders(), Map.class);
    return new MessageWithDestination(eventBean.getDestination(),new MessageImpl(payload, headers), null);
  }
}
