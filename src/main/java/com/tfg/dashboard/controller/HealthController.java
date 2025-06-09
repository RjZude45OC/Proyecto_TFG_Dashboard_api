package com.tfg.dashboard.controller;

import com.tfg.dashboard.model.SystemMetrics;
import com.tfg.dashboard.service.DiscordWebhookService;
import com.tfg.dashboard.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check")
public class HealthController {
    private final SystemMonitorService systemMonitorService;
    private final DiscordWebhookService discordWebhookService;

    @GetMapping("")
    @Operation(summary = "Hello endpoint",
            description = "Returns a simple hello message")
    public String helloworld() {
        return "hello";
    }

    @GetMapping("/test")
    @Operation(summary = "Test Discord webhook",
            description = "Sends a test message to Discord webhook with current system metrics")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Test message sent successfully"),
            @ApiResponse(responseCode = "500", description = "Failed to send test message")
    })
    public ResponseEntity<Map<String, Object>> testWebhook() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Get current metrics
            SystemMetrics metrics = systemMonitorService.getSystemMetrics();

            // Send test message to Discord
            discordWebhookService.sendMetricsToDiscord(metrics);

            // Prepare success response
            response.put("status", "success");
            response.put("message", "Test webhook message sent successfully");
            response.put("timestamp", Instant.now().toString());
            response.put("metrics", Map.of(
                    "cpu", String.format("%.2f%%", metrics.getCpu().getSystemCpuLoad()),
                    "memory", String.format("%.2f%%", metrics.getMemory().getMemoryUsagePercentage())
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Prepare error response
            response.put("status", "error");
            response.put("message", "Failed to send test webhook message");
            response.put("error", e.getMessage());
            response.put("timestamp", Instant.now().toString());

            return ResponseEntity.internalServerError().body(response);
        }
    }

}
