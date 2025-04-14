package com.tfg.dashboard.service;

import com.tfg.dashboard.model.SystemMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "metrics.scheduler.enabled", havingValue = "true", matchIfMissing = false)
public class scheduler {

    private final SystemMonitorService systemMonitorService;

    @Scheduled(fixedRateString = "${metrics.scheduler.interval:60000}")
    public void logSystemMetrics() {
        if (log.isInfoEnabled()) {
            SystemMetrics metrics = systemMonitorService.getSystemMetrics();
            log.info("System CPU load: {}%", String.format("%.2f", metrics.getCpu().getSystemCpuLoad()));
            log.info("Memory usage: {}%", String.format("%.2f", metrics.getMemory().getMemoryUsagePercentage()));
            metrics.getDisks().forEach(disk ->
                    log.info("Disk {} usage: {}%", disk.getName(), String.format("%.2f", disk.getUsagePercentage())));
        }
    }
}