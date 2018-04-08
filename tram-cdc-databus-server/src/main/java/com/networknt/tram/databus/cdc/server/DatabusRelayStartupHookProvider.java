package com.networknt.tram.databus.cdc.server;

import com.linkedin.databus2.relay.DatabusRelayMain;
import com.linkedin.databus2.relay.config.PhysicalSourceStaticConfig;
import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;

import com.linkedin.databus.container.netty.HttpRelay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CdcServer StartupHookProvider. start cdc service
 */
public class DatabusRelayStartupHookProvider implements StartupHookProvider {

    protected static Logger logger = LoggerFactory.getLogger(DatabusRelayStartupHookProvider.class);

    static String RELAY_CONFIG_NAME = "relay";
    static RelayConfig relayConfig = (RelayConfig) Config.getInstance().getJsonObjectConfig(RELAY_CONFIG_NAME, RelayConfig.class);

    @Override
    @SuppressWarnings("unchecked")
    public void onStartup() {
        HttpRelay.Cli cli = new HttpRelay.Cli();

        try {
            cli.setDefaultPhysicalSrcConfigFiles(relayConfig.getSrcConfigFiles());
            cli.processCommandLineArgs(relayConfig.getProcessProperites());
            cli.parseRelayConfig();
            // Process the startup properties and load configuration
            PhysicalSourceStaticConfig[] pStaticConfigs = cli.getPhysicalSourceStaticConfigs();
            HttpRelay.StaticConfig staticConfig = cli.getRelayConfigBuilder().build();

            // Create and initialize the server instance
            DatabusRelayMain serverContainer = new DatabusRelayMain(staticConfig, pStaticConfigs);

            serverContainer.initProducers();
            serverContainer.registerShutdownHook();
            serverContainer.startAndBlock();
        } catch (Exception e) {
            logger.error("dataBus relay error:" + e);
        }

    }
}
