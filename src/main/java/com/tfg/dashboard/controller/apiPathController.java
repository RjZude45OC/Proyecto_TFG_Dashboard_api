package com.tfg.dashboard.controller;

import com.tfg.dashboard.model.*;
import com.tfg.dashboard.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "System Monitor", description = "API for monitoring system hardware metrics")
public class apiPathController {
    private final SystemMonitorService systemMonitorService;

    public apiPathController(SystemMonitorService systemMonitorService) {
        this.systemMonitorService = systemMonitorService;
    }

    @GetMapping
    @Operation(summary = "Get all system metrics", description = "Returns complete system metrics including CPU, memory, disk, and network")
    public ResponseEntity<SystemMetrics> getAllMetrics() {
        return ResponseEntity.ok(systemMonitorService.getSystemMetrics());
    }

    @GetMapping("/cpu")
    @Operation(summary = "Get CPU metrics", description = "Returns detailed CPU metrics including load and processor information")
    public ResponseEntity<CpuMetrics> getCpuMetrics() {
        return ResponseEntity.ok(systemMonitorService.getCpuMetrics());
    }

    @GetMapping("/memory")
    @Operation(summary = "Get memory metrics", description = "Returns detailed memory usage metrics")
    public ResponseEntity<MemoryMetrics> getMemoryMetrics() {
        return ResponseEntity.ok(systemMonitorService.getMemoryMetrics());
    }

    @GetMapping("/disks")
    @Operation(summary = "Get disk metrics", description = "Returns metrics for all disk storage devices")
    public ResponseEntity<List<DiskMetrics>> getDiskMetrics() {
        return ResponseEntity.ok(systemMonitorService.getDiskMetrics());
    }

    @GetMapping("/network")
    @Operation(summary = "Get network metrics", description = "Returns metrics for all network interfaces")
    public ResponseEntity<NetworkMetrics> getNetworkMetrics() {
        return ResponseEntity.ok(systemMonitorService.getNetworkMetrics());
    }

}
