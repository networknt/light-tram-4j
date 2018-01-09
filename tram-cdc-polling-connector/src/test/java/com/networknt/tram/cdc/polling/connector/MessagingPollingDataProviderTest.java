package com.networknt.tram.cdc.polling.connector;

import com.networknt.eventuate.jdbc.EventuateSchema;
import com.networknt.service.SingletonServiceFactory;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class MessagingPollingDataProviderTest {

    @Test
    public void testSchema(){
        EventuateSchema schema = (EventuateSchema) SingletonServiceFactory.getBean(EventuateSchema.class);
        MessagePollingDataProvider pollingDataProvider= (MessagePollingDataProvider) SingletonServiceFactory.getBean(MessagePollingDataProvider.class);
        assertTrue(pollingDataProvider.table().contains("TRAM"));
    }

}
