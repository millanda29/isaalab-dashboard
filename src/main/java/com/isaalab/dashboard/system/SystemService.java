package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class SystemService {

    public SystemInfo getInfo() throws IOException {
        double cpu = getCpuUsage();
        long memTotal = getMemoryTotal();
        long memUsed = getMemoryUsed();
        String uptime = getUptime();

        File root = new File("/");
        long diskTotal = root.getTotalSpace();
        long diskUsed = diskTotal - root.getFreeSpace();
        double diskPercent = diskTotal > 0 ? (diskUsed * 100.0 / diskTotal) : 0;
        double memPercent = memTotal > 0 ? (memUsed * 100.0 / memTotal) : 0;

        return new SystemInfo(
                cpu,
                memPercent,
                memUsed,
                memTotal,
                diskPercent,
                diskUsed,
                diskTotal,
                uptime
        );
    }

    private double getCpuUsage() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "sh", "-c", "top -bn1 | grep 'Cpu(s)' | awk '{print $2}'"
        );
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null) {
                return Double.parseDouble(line.replace(",", "."));
            }
        }
        return 0.0;
    }

    private long getMemoryTotal() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "sh", "-c", "free -b | grep Mem | awk '{print $2}'"
        );
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null) {
                return Long.parseLong(line.trim());
            }
        }
        return 0;
    }

    private long getMemoryUsed() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "sh", "-c", "free -b | grep Mem | awk '{print $3}'"
        );
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            if (line != null) {
                return Long.parseLong(line.trim());
            }
        }
        return 0;
    }

    private String getUptime() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "uptime -p");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return line != null ? line.replace("up ", "") : "unknown";
        }
    }
}
