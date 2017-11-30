package com.networknt.tram.message.producer;


import com.networknt.tram.message.common.Message;

public interface MessageProducer {

  /**
   * Send a message
   * @param destination the destination channel
   * @param message the message to send
   * @see Message
   */
  void send(String destination, Message message);

}
