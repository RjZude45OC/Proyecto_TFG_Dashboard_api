package com.tfg.dashboard.model;

import lombok.Data;
import java.util.Map;

@Data
public class NetworkMetrics {
    private Map<String, NetworkInterfaceMetrics> interfaces;
}
