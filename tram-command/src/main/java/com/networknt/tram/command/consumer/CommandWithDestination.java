package com.networknt.tram.command.consumer;


import com.networknt.config.JsonMapper;
import com.networknt.tram.command.common.Command;

// Todo - replace CommandToSendWithThis

public class CommandWithDestination {
  private final String destinationChannel;
  private final String resource;
  private final Command command;

  @Override
  public String toString() {
    return JsonMapper.toJson(this);
  }

  public CommandWithDestination(String destinationChannel, String resource, Command command) {
    this.destinationChannel = destinationChannel;
    this.resource = resource;
    this.command = command;
  }

  public String getDestinationChannel() {
    return destinationChannel;
  }

  public String getResource() {
    return resource;
  }

  public Command getCommand() {
    return command;
  }
}
