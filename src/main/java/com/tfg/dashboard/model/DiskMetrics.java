package com.tfg.dashboard.model;

import lombok.Data;

@Data
public class DiskMetrics {
    private String name;
    private String mountPoint;
    private long totalSpace;
    private long usableSpace;
    private long usedSpace;
    private double usagePercentage;
}
