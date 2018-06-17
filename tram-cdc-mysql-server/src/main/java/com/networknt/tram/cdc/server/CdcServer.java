package com.networknt.tram.cdc.server;

import com.networknt.server.HandlerProvider;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * CdcServer path handler provider with only one dummy path to return OK! The server is
 * designed to load the startup provider only in order to pickup messages from message
 * table and publish to Kafka.
 *
 * @author Steve Hu
 */
public class CdcServer implements HandlerProvider {
    @Override
    public HttpHandler getHandler() {
        return Handlers.path()
                .addPrefixPath("/", exchange -> exchange.getResponseSender().send("OK!")
                );
    }
}
