package com.tfg.dashboard.model;

import lombok.Data;

@Data
public class NetworkInterfaceMetrics {
    private String name;
    private String displayName;
    private long bytesReceived;
    private long bytesSent;
    private long packetsReceived;
    private long packetsSent;
    private long inErrors;
    private long outErrors;

    public void setName(String name) {
        this.name = name;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setBytesReceived(long bytesRecv) {
        this.bytesReceived = bytesRecv;
    }
    public void setPacketsSent(long packetsSent) {
        this.packetsSent = packetsSent;
    }

    public void setInErrors(long inErrors) {
        this.inErrors = inErrors;
    }

    public void setOutErrors(long outErrors) {
        this.outErrors = outErrors;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public void setPacketsReceived(long packetsReceived) {
        this.packetsReceived = packetsReceived;
    }
}
