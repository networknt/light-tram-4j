package com.networknt.tram.command;

import com.networknt.eventuate.common.impl.JSonMapper;
import org.junit.Test;

public class MyTestCommandJSonIT {

  @Test
  public void shouldSerde() {
    MyTestCommand x = new MyTestCommand();
    String s = JSonMapper.toJson(x);
    MyTestCommand x2 = JSonMapper.fromJson(s, MyTestCommand.class);

  }
}
