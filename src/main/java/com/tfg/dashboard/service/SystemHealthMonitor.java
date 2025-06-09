package com.tfg.dashboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemHealthMonitor {
    static final Logger log = LoggerFactory.getLogger(scheduler.class);
    private final SystemMonitorService systemMonitorService;
    private final DiscordWebhookService discordWebhookService;
    private boolean criticalAlertSent = false;

    @Scheduled(fixedRate = 60000) // Check every minute
    public void monitorSystemHealth() {
        var metrics = systemMonitorService.getSystemMetrics();
        double cpuUsage = metrics.getCpu().getSystemCpuLoad();
        double memoryUsage = metrics.getMemory().getMemoryUsagePercentage();
        double systemHealth = 100 - ((cpuUsage + memoryUsage) / 2);

        if (systemHealth <= 25 && !criticalAlertSent) {
            log.warn("System health is critical ({}%). Sending alert.", String.format("%.2f", systemHealth));
            discordWebhookService.sendMetricsToDiscord(metrics);
            criticalAlertSent = true;
        } else if (systemHealth > 25) {
            criticalAlertSent = false;
        }
    }
}
