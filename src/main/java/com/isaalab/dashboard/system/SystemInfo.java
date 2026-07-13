package com.isaalab.dashboard.system;

public record SystemInfo(
        double cpuPercent,
        double memoryPercent,
        long memoryUsed,
        long memoryTotal,
        double diskPercent,
        long diskUsed,
        long diskTotal,
        String uptime
) {}
