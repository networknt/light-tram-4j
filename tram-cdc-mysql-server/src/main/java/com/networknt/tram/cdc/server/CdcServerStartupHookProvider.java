package com.networknt.tram.cdc.server;


import com.networknt.eventuate.server.common.EventTableChangesToAggregateTopicTranslator;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.cdc.mysql.connector.MessageWithDestination;

import com.networknt.server.StartupHookProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CdcServer StartupHookProvider. start cdc service
 */
public class CdcServerStartupHookProvider implements StartupHookProvider {

    private static final Logger logger = LoggerFactory.getLogger(CdcServerStartupHookProvider.class);

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup() {

        EventTableChangesToAggregateTopicTranslator<MessageWithDestination> translator = SingletonServiceFactory.getBean(EventTableChangesToAggregateTopicTranslator.class);
        translator.start();
        logger.info("CdcServerStartupHookProvider is called");
    }
}
