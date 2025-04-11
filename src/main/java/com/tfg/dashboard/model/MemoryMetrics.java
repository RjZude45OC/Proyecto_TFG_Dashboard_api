package com.tfg.dashboard.model;

import lombok.Data;

@Data
public class MemoryMetrics {
    private long totalMemory;
    private long availableMemory;
    private long usedMemory;
    private double memoryUsagePercentage;
}
