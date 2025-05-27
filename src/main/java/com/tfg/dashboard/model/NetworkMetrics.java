package com.tfg.dashboard.model;

import lombok.Data;
import java.util.Map;

@Data
public class NetworkMetrics {
    private Map<String, NetworkInterfaceMetrics> interfaces;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private long timestamp;

    public void setInterfaces(Map<String, NetworkInterfaceMetrics> interfaceMetricsMap) {
        interfaces = interfaceMetricsMap;
    }

    public Map<String, NetworkInterfaceMetrics> getInterfaces() {
        return interfaces;
    }
}
