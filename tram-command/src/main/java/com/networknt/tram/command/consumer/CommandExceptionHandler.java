package com.networknt.tram.command.consumer;


import com.networknt.tram.message.common.Message;

import java.util.List;

public class CommandExceptionHandler {
  public List<Message> invoke(Throwable cause) {
    throw new UnsupportedOperationException();
  }
}
