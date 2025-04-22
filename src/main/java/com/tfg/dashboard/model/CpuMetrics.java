package com.tfg.dashboard.model;

import lombok.Data;
import java.util.List;

@Data
public class CpuMetrics {
    private double systemCpuLoad;
    private int availableProcessors;
    private List<Double> perProcessorLoad;
    private double systemLoadAverage;

    public CpuMetrics(double systemCpuLoad, int availableProcessors, List<Double> perProcessorLoad, double systemLoadAverage) {
        this.systemCpuLoad = systemCpuLoad;
        this.availableProcessors = availableProcessors;
        this.perProcessorLoad = perProcessorLoad;
        this.systemLoadAverage = systemLoadAverage;
    }

    public CpuMetrics() {
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public int getAvailableProcessors() {
        return availableProcessors;
    }

    public void setAvailableProcessors(int availableProcessors) {
        this.availableProcessors = availableProcessors;
    }

    public List<Double> getPerProcessorLoad() {
        return perProcessorLoad;
    }

    public void setPerProcessorLoad(List<Double> perProcessorLoad) {
        this.perProcessorLoad = perProcessorLoad;
    }

    public double getSystemLoadAverage() {
        return systemLoadAverage;
    }

    public void setSystemLoadAverage(double systemLoadAverage) {
        this.systemLoadAverage = systemLoadAverage;
    }

}
