package com.networknt.tram.cdc.server;

import com.networknt.config.Config;

import com.networknt.eventuate.cdc.polling.PollingCdcKafkaPublisher;
import com.networknt.eventuate.kafka.KafkaConfig;
import com.networknt.eventuate.kafka.producer.EventuateKafkaProducer;
import com.networknt.eventuate.server.common.*;
import com.networknt.server.StartupHookProvider;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.cdc.mysql.connector.MessageWithDestination;
import com.networknt.tram.cdc.polling.connector.MessagePollingDataProvider;
import com.networknt.tram.cdc.polling.connector.PollingCdcProcessor;
import com.networknt.tram.cdc.polling.connector.PollingDao;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import javax.sql.DataSource;

/**
 * CdcServer StartupHookProvider. start cdc service
 */
public class CdcServerStartupHookProvider implements StartupHookProvider {
    static String CDC_CONFIG_NAME = "cdc";
    static CdcConfig cdcConfig = (CdcConfig) Config.getInstance().getJsonObjectConfig(CDC_CONFIG_NAME, CdcConfig.class);
    static String KAFKA_CONFIG_NAME = "kafka";
    static KafkaConfig kafkaConfig = (KafkaConfig) Config.getInstance().getJsonObjectConfig(KAFKA_CONFIG_NAME, KafkaConfig.class);
    static String PULLING_CONFIG_NAME = "pulling";
    static PullingConfig pullingConfig = (PullingConfig) Config.getInstance().getJsonObjectConfig(PULLING_CONFIG_NAME, PullingConfig.class);

    public static CuratorFramework curatorFramework;
    public static EventTableChangesToAggregateTopicTranslator<MessageWithDestination> translator;

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup() {

        curatorFramework = makeStartedCuratorClient(cdcConfig.getZookeeper());


        MessagePollingDataProvider pollingDataProvider= (MessagePollingDataProvider) SingletonServiceFactory.getBean(MessagePollingDataProvider.class);
        if (pullingConfig != null) pollingDataProvider.reset(pullingConfig.getTableName(), pullingConfig.getIdField(), pullingConfig.getPublishedField(),
                                         pullingConfig.getHeaders(), pullingConfig.getDestination(), pullingConfig.getPayload());
        EventuateKafkaProducer eventuateKafkaProducer = new EventuateKafkaProducer();
        PublishingStrategy<MessageWithDestination> publishingStrategy = SingletonServiceFactory.getBean(PublishingStrategy.class);

        CdcKafkaPublisher<MessageWithDestination>  messageCdcKafkaPublisher = new PollingCdcKafkaPublisher<>( kafkaConfig.getBootstrapServers(), publishingStrategy);
        DataSource ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);

        PollingDao pollingDao =  new PollingDao(pollingDataProvider, ds,
                cdcConfig.getMaxEventsPerPolling(),
                cdcConfig.getMaxAttemptsForPolling(),
                cdcConfig.getPollingRetryIntervalInMilliseconds());


        CdcProcessor<MessageWithDestination> pollingCdcProcessor = new PollingCdcProcessor<>(pollingDao, cdcConfig.getPollingIntervalInMilliseconds());

        translator = new EventTableChangesToAggregateTopicTranslator<>(messageCdcKafkaPublisher, pollingCdcProcessor, curatorFramework, cdcConfig );
        translator.start();

        System.out.println("CdcServerStartupHookProvider is called");
    }

    CuratorFramework makeStartedCuratorClient(String connectionString) {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(2000, 6, 2000);
        CuratorFramework client = CuratorFrameworkFactory.
                builder().connectString(connectionString)
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        return client;
    }
}
