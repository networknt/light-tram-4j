package com.networknt.tram.command;

import com.networknt.tram.command.common.Command;

@CommandDestination("destination")
public class MyTestCommand implements Command {

  private String name;

  public MyTestCommand() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
