package com.tfg.dashboard.model;

import lombok.Data;
import java.util.List;

@Data
public class SystemMetrics {
    private CpuMetrics cpu;
    private MemoryMetrics memory;
    private List<DiskMetrics> disks;
    private NetworkMetrics network;
    private String os;

    public SystemMetrics(CpuMetrics cpu, List<DiskMetrics> disks, MemoryMetrics memory, NetworkMetrics network,String os) {
        this.cpu = cpu;
        this.disks = disks;
        this.memory = memory;
        this.network = network;
        this.os = os;
    }

    public SystemMetrics() {
    }

    public CpuMetrics getCpu() {
        return cpu;
    }

    public void setCpu(CpuMetrics cpu) {
        this.cpu = cpu;
    }

    public MemoryMetrics getMemory() {
        return memory;
    }

    public void setMemory(MemoryMetrics memory) {
        this.memory = memory;
    }

    public List<DiskMetrics> getDisks() {
        return disks;
    }

    public void setDisks(List<DiskMetrics> disks) {
        this.disks = disks;
    }

    public NetworkMetrics getNetwork() {
        return network;
    }

    public void setNetwork(NetworkMetrics network) {
        this.network = network;
    }

    public String getOs(){ return os; }
    public void setOs(String os){ this.os = os;};
}

