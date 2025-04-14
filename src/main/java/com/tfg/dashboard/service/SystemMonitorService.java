package com.tfg.dashboard.service;

import com.tfg.dashboard.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SystemMonitorService {
    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem os;

    public SystemMonitorService() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.os = systemInfo.getOperatingSystem();
    }

    public SystemMetrics getSystemMetrics() {
        SystemMetrics metrics = new SystemMetrics();
        metrics.setCpu(getCpuMetrics());
        metrics.setMemory(getMemoryMetrics());
        metrics.setDisks(getDiskMetrics());
        metrics.setNetwork(getNetworkMetrics());
        return metrics;
    }

    public CpuMetrics getCpuMetrics() {
        CentralProcessor processor = hardware.getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();

        // Wait a bit to get a valid CPU load calculation
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        CpuMetrics cpuMetrics = new CpuMetrics();
        long[] prevTicksPerc = processor.getSystemCpuLoadTicks();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[] currTicksPerc = processor.getSystemCpuLoadTicks();
        double systemCpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicksPerc) * 100;
        cpuMetrics.setSystemCpuLoad(systemCpuLoad);

        cpuMetrics.setAvailableProcessors(processor.getLogicalProcessorCount());

        double[] loadAverage = processor.getSystemLoadAverage(1);
        cpuMetrics.setSystemLoadAverage(loadAverage[0]);

        // Get per-processor load
        long[][] prevProcTicks = processor.getProcessorCpuLoadTicks();
        try {
            Thread.sleep(500); // Only sleep once if this is in sequence with the above code
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long[][] currProcTicks = processor.getProcessorCpuLoadTicks();
        double[] loadPerProcessor = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        List<Double> perProcessorLoad = new ArrayList<>();
        for (double load : loadPerProcessor) {
            perProcessorLoad.add(load * 100);
        }
        cpuMetrics.setPerProcessorLoad(perProcessorLoad);
        return cpuMetrics;
    }

    public MemoryMetrics getMemoryMetrics() {
        GlobalMemory memory = hardware.getMemory();
        long total = memory.getTotal();
        long available = memory.getAvailable();
        long used = total - available;

        MemoryMetrics memoryMetrics = new MemoryMetrics();
        memoryMetrics.setTotalMemory(total);
        memoryMetrics.setAvailableMemory(available);
        memoryMetrics.setUsedMemory(used);
        memoryMetrics.setMemoryUsagePercentage((double) used / total * 100);

        return memoryMetrics;
    }

    public List<DiskMetrics> getDiskMetrics() {
        List<DiskMetrics> diskMetricsList = new ArrayList<>();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fileStores = fileSystem.getFileStores();

        for (OSFileStore store : fileStores) {
            DiskMetrics diskMetrics = new DiskMetrics();
            diskMetrics.setName(store.getName());
            diskMetrics.setMountPoint(store.getMount());

            long total = store.getTotalSpace();
            long usable = store.getUsableSpace();
            long used = total - usable;

            diskMetrics.setTotalSpace(total);
            diskMetrics.setUsableSpace(usable);
            diskMetrics.setUsedSpace(used);
            diskMetrics.setUsagePercentage(total > 0 ? (double) used / total * 100 : 0);

            diskMetricsList.add(diskMetrics);
        }

        return diskMetricsList;
    }

    public NetworkMetrics getNetworkMetrics() {
        NetworkMetrics networkMetrics = new NetworkMetrics();
        Map<String, NetworkInterfaceMetrics> interfaceMetricsMap = new HashMap<>();

        List<NetworkIF> networkIFs = hardware.getNetworkIFs();
        for (NetworkIF networkIF : networkIFs) {
            // Refresh to get current values
            networkIF.updateAttributes();

            NetworkInterfaceMetrics interfaceMetrics = new NetworkInterfaceMetrics();
            interfaceMetrics.setName(networkIF.getName());
            interfaceMetrics.setDisplayName(networkIF.getDisplayName());
            interfaceMetrics.setBytesReceived(networkIF.getBytesRecv());
            interfaceMetrics.setBytesSent(networkIF.getBytesSent());
            interfaceMetrics.setPacketsReceived(networkIF.getPacketsRecv());
            interfaceMetrics.setPacketsSent(networkIF.getPacketsSent());
            interfaceMetrics.setInErrors(networkIF.getInErrors());
            interfaceMetrics.setOutErrors(networkIF.getOutErrors());

            interfaceMetricsMap.put(networkIF.getName(), interfaceMetrics);
        }

        networkMetrics.setInterfaces(interfaceMetricsMap);
        return networkMetrics;
    }
}
