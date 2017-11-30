package com.networknt.tram.message.consumer;

import com.networknt.tram.message.common.Message;

import java.util.function.Consumer;

public interface MessageHandler extends Consumer<Message> {
}
