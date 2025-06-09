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

            webhookMessage.put("content", "<@&" + roleId + "> System Update");

            Map<String, Object> embed = new HashMap<>();
            embed.put("title", "System Metrics Report");
            embed.put("color", 3447003); // Blue color

            java.util.List<Map<String, Object>> fields = new java.util.ArrayList<>();

            // CPU Metrics
            Map<String, Object> cpuField = new HashMap<>();
            cpuField.put("name", "CPU Usage");
            cpuField.put("value", df.format(metrics.getCpu().getSystemCpuLoad()) + "%");
            cpuField.put("inline", true);
            fields.add(cpuField);

            // Memory Metrics
            Map<String, Object> memoryField = new HashMap<>();
            memoryField.put("name", "Memory Usage");
            memoryField.put("value", df.format(metrics.getMemory().getMemoryUsagePercentage()) + "%");
            memoryField.put("inline", true);
            fields.add(memoryField);

            // Disk Metrics
            metrics.getDisks().forEach(disk -> {
                Map<String, Object> diskField = new HashMap<>();
                diskField.put("name", "Disk: " + disk.getName());
                diskField.put("value", df.format(disk.getUsagePercentage()) + "% used");
                diskField.put("inline", true);
                fields.add(diskField);
            });

            embed.put("fields", fields);

            //timestamp
            embed.put("timestamp", java.time.Instant.now().toString());

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