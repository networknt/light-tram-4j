package com.networknt.tram.cdc.mysql.connector;

import com.networknt.eventuate.server.common.BinLogEvent;
import com.networknt.eventuate.server.common.BinlogFileOffset;
import com.networknt.tram.message.common.MessageImpl;

public class MessageWithDestination implements BinLogEvent {
  private final String destination;
  private final MessageImpl message;
  private BinlogFileOffset binlogFileOffset;

  public MessageWithDestination(String destination, MessageImpl message, BinlogFileOffset binlogFileOffset) {
    this.destination = destination;
    this.message = message;
    this.binlogFileOffset = binlogFileOffset;
  }

  public String getDestination() {
    return destination;
  }

  public MessageImpl getMessage() {
    return message;
  }

  public BinlogFileOffset getBinlogFileOffset() {
    return binlogFileOffset;
  }
}
