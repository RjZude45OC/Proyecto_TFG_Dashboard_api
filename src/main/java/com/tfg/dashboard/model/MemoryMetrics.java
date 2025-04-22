package com.tfg.dashboard.model;

import lombok.Data;

@Data
public class MemoryMetrics {
    private long totalMemory;
    private long availableMemory;
    private long usedMemory;
    private double memoryUsagePercentage;

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public void setAvailableMemory(long availableMemory) {
        this.availableMemory = availableMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public void setMemoryUsagePercentage(double memoryUsagePercentage) {
        this.memoryUsagePercentage = memoryUsagePercentage;
    }

    public double getMemoryUsagePercentage() {
        return memoryUsagePercentage;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getAvailableMemory() {
        return availableMemory;
    }

    public long getUsedMemory() {
        return usedMemory;
    }
}
