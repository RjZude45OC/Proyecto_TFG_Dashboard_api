package com.tfg.dashboard.service;

import com.tfg.dashboard.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static com.tfg.dashboard.service.scheduler.log;

@Service
@Slf4j
public class SystemMonitorService {
    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem os;
    private final AtomicReference<CpuMetrics> cachedCpuMetrics = new AtomicReference<>(new CpuMetrics());
    private final Lock cpuMetricsLock = new ReentrantLock();

    private long[] prevTicks;
    private long[][] prevProcTicks;

    public SystemMonitorService() {
        SystemInfo systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.os = systemInfo.getOperatingSystem();
    }

    @PostConstruct
    public void init() {
        CentralProcessor processor = hardware.getProcessor();
        this.prevTicks = processor.getSystemCpuLoadTicks();
        this.prevProcTicks = processor.getProcessorCpuLoadTicks();
        updateCpuMetricsInternal();
    }

    @Scheduled(fixedRate = 3000)
    public void updateCpuMetrics() {
        cpuMetricsLock.lock();
        try {
            updateCpuMetricsInternal();
        } finally {
            cpuMetricsLock.unlock();
        }
    }

    private void updateCpuMetricsInternal() {
        CentralProcessor processor = hardware.getProcessor();
        long[] currTicks = processor.getSystemCpuLoadTicks();
        long[][] currProcTicks = processor.getProcessorCpuLoadTicks();

        CpuMetrics cpuMetrics = new CpuMetrics();
        cpuMetrics.setSystemCpuLoad(processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100);
        cpuMetrics.setAvailableProcessors(processor.getLogicalProcessorCount());
        double[] loadAverage = processor.getSystemLoadAverage(1);
        cpuMetrics.setSystemLoadAverage(loadAverage[0]);

        double[] loadPerProcessor = processor.getProcessorCpuLoadBetweenTicks(prevProcTicks);
        List<Double> perProcessorLoad = new ArrayList<>();
        for (double load : loadPerProcessor) {
            perProcessorLoad.add(load * 100);
        }
        cpuMetrics.setPerProcessorLoad(perProcessorLoad);

        cachedCpuMetrics.set(cpuMetrics);
        this.prevTicks = currTicks;
        this.prevProcTicks = currProcTicks;

        log.debug("Updated CPU metrics: system load {}%, processors: {}",
                String.format("%.2f", cpuMetrics.getSystemCpuLoad()),
                cpuMetrics.getAvailableProcessors());
    }

    public SystemMetrics getSystemMetrics() {
        SystemMetrics metrics = new SystemMetrics();
        metrics.setCpu(getCpuMetrics());
        metrics.setMemory(getMemoryMetrics());
        metrics.setDisks(getDiskMetrics());
        metrics.setNetwork(getNetworkMetrics());
        metrics.setOs(String.valueOf(os));
        return metrics;
    }

    public CpuMetrics getCpuMetrics() {
        return cachedCpuMetrics.get();
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
        long timestamp = System.currentTimeMillis();
        NetworkMetrics networkMetrics = new NetworkMetrics();
        java.util.Map<String, NetworkInterfaceMetrics> interfaceMetricsMap = new java.util.HashMap<>();

        List<oshi.hardware.NetworkIF> networkIFs = hardware.getNetworkIFs();
        for (oshi.hardware.NetworkIF networkIF : networkIFs) {
            networkIF.updateAttributes();
            NetworkInterfaceMetrics interfaceMetrics = getNetworkInterfaceMetrics(networkIF);
            interfaceMetricsMap.put(networkIF.getName(), interfaceMetrics);
        }

        networkMetrics.setInterfaces(interfaceMetricsMap);
        networkMetrics.setTimestamp(timestamp);
        return networkMetrics;
    }

    private static NetworkInterfaceMetrics getNetworkInterfaceMetrics(oshi.hardware.NetworkIF networkIF) {
        NetworkInterfaceMetrics interfaceMetrics = new NetworkInterfaceMetrics();
        interfaceMetrics.setName(networkIF.getName());
        interfaceMetrics.setDisplayName(networkIF.getDisplayName());
        interfaceMetrics.setBytesReceived(networkIF.getBytesRecv());
        interfaceMetrics.setBytesSent(networkIF.getBytesSent());
        interfaceMetrics.setPacketsReceived(networkIF.getPacketsRecv());
        interfaceMetrics.setPacketsSent(networkIF.getPacketsSent());
        interfaceMetrics.setInErrors(networkIF.getInErrors());
        interfaceMetrics.setOutErrors(networkIF.getOutErrors());
        return interfaceMetrics;
    }
}