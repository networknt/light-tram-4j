package com.networknt.tram.databus.cdc.server;

public class RelayConfig {
    private String[] processProperites;
    private String srcConfigFiles;

    public String[] getProcessProperites() {
        return processProperites;
    }

    public void setProcessProperites(String[] processProperites) {
        this.processProperites = processProperites;
    }

    public String getSrcConfigFiles() {
        return srcConfigFiles;
    }

    public void setSrcConfigFiles(String srcConfigFiles) {
        this.srcConfigFiles = srcConfigFiles;
    }
}
