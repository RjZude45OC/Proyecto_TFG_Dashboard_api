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

    public void setName(String name) {
        this.name = name;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    public void setUsableSpace(long usableSpace) {
        this.usableSpace = usableSpace;
    }

    public void setUsedSpace(long usedSpace) {
        this.usedSpace = usedSpace;
    }

    public void setUsagePercentage(double usagePercentage) {
        this.usagePercentage = usagePercentage;
    }
}
