package com.networknt.tram.command.consumer;

import com.networknt.config.JsonMapper;

public class ReplyDestination {

  public final String destination;
  public final String partitionKey;

  public ReplyDestination(String destination, String partitionKey) {
    this.partitionKey = partitionKey;
    this.destination = destination;
  }

  @Override
  public String toString() {
    return JsonMapper.toJson(this);
  }
}
