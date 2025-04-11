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
}
