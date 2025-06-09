package com.tfg.dashboard.service;

import com.tfg.dashboard.model.SystemMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import static com.tfg.dashboard.service.scheduler.log;

@Service
@Slf4j
public class DiscordWebhookService {

    @Value("${discord.webhook.url}")
    private String webhookUrl;

    @Value("${discord.role.id}")
    private String roleId;

    private final RestTemplate restTemplate;
    private final DecimalFormat df = new DecimalFormat("#.##");

    public DiscordWebhookService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendMetricsToDiscord(SystemMetrics metrics) {
        try {
            Map<String, Object> webhookMessage = new HashMap<>();
            Map<String, Object> embed = new HashMap<>();

            // Calculate system health
            double cpuUsage = metrics.getCpu().getSystemCpuLoad();
            double memoryUsage = metrics.getMemory().getMemoryUsagePercentage();
            double systemHealth = 100 - ((cpuUsage + memoryUsage) / 2);

            // Set message content and color based on system health
            String content;
            int color;
            String title;

            if (systemHealth > 75) {
                content = "<@&" + roleId + "> System Status: Excellent";
                color = 0x28a745; // Green
                title = "✅ System Metrics Report - Optimal Performance";
            } else if (systemHealth > 50) {
                content = "<@&" + roleId + "> System Status: Good";
                color = 0x218838; // Dark Green
                title = "✅ System Metrics Report - Good Performance";
            } else if (systemHealth > 25) {
                content = "<@&" + roleId + "> Warning: System performance is degrading";
                color = 0xffc107; // Yellow
                title = "⚠️ System Metrics Report - Performance Degrading";
            } else {
                content = "<@&" + roleId + "> CRITICAL: System health is critical! Immediate attention required!";
                color = 0xdc3545; // Red
                title = "🚨 System Metrics Report - CRITICAL STATUS";
            }

            webhookMessage.put("content", content);
            embed.put("title", title);
            embed.put("color", color);

            java.util.List<Map<String, Object>> fields = new java.util.ArrayList<>();

            // CPU Metrics with warning indicators
            Map<String, Object> cpuField = new HashMap<>();
            cpuField.put("name", "CPU Usage");
            String cpuIndicator = cpuUsage > 80 ? "🚨" : cpuUsage > 60 ? "⚠️" : "✅";
            cpuField.put("value", cpuIndicator + " " + df.format(cpuUsage) + "%");
            cpuField.put("inline", true);
            fields.add(cpuField);

            // Memory Metrics with warning indicators
            Map<String, Object> memoryField = new HashMap<>();
            memoryField.put("name", "Memory Usage");
            String memoryIndicator = memoryUsage > 80 ? "🚨" : memoryUsage > 60 ? "⚠️" : "✅";
            memoryField.put("value", memoryIndicator + " " + df.format(memoryUsage) + "%");
            memoryField.put("inline", true);
            fields.add(memoryField);

            // System Health Score
            Map<String, Object> healthField = new HashMap<>();
            healthField.put("name", "System Health Score");
            String healthIndicator = systemHealth < 25 ? "🚨" : systemHealth < 50 ? "⚠️" : "✅";
            healthField.put("value", healthIndicator + " " + df.format(systemHealth) + "%");
            healthField.put("inline", true);
            fields.add(healthField);

            // Disk Metrics
            metrics.getDisks().forEach(disk -> {
                Map<String, Object> diskField = new HashMap<>();
                double usagePercentage = disk.getUsagePercentage();
                String diskIndicator = usagePercentage > 90 ? "🚨" : usagePercentage > 75 ? "⚠️" : "✅";

                double totalGB = disk.getTotalSpace() / (1024.0 * 1024 * 1024);
                double usedGB = disk.getUsedSpace() / (1024.0 * 1024 * 1024);

                diskField.put("name", "Disk: " + disk.getName());
                diskField.put("value", String.format("%s %.2f GB / %.2f GB (%.1f%%)",
                        diskIndicator, usedGB, totalGB, usagePercentage));
                diskField.put("inline", true);
                fields.add(diskField);
            });

            embed.put("fields", fields);

            // Add timestamp
            embed.put("timestamp", java.time.Instant.now().toString());

            // Add footer with hostname or additional info
            Map<String, String> footer = new HashMap<>();
            footer.put("text", "Server Monitor - " + metrics.getOs());
            embed.put("footer", footer);

            // Add the embed to the message
            webhookMessage.put("embeds", java.util.Collections.singletonList(embed));

            // Send the message
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            org.springframework.http.HttpEntity<Map<String, Object>> request =
                    new org.springframework.http.HttpEntity<>(webhookMessage, headers);

            restTemplate.postForEntity(webhookUrl, request, String.class);

            log.debug("Successfully sent metrics to Discord webhook");
        } catch (Exception e) {
            log.error("Failed to send metrics to Discord webhook", e);
        }
    }
}