package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

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

    public List<Map<String, String>> getPorts() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "ss -tlnp | tail -n +2");
        Process process = pb.start();
        List<Map<String, String>> list = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 4) {
                    Map<String, String> p = new java.util.LinkedHashMap<>();
                    p.put("proto", parts[0]);
                    p.put("recvQ", parts[1]);
                    p.put("sendQ", parts[2]);
                    p.put("local", parts[3]);
                    p.put("peer", parts.length > 4 ? parts[4] : "");
                    p.put("process", parts.length > 5 ? parts[5] : "");
                    list.add(p);
                }
            }
        }
        return list;
    }

    public List<Map<String, String>> getServices() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "systemctl list-units --type=service --no-pager --no-legend | head -50");
        Process process = pb.start();
        List<Map<String, String>> list = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3) {
                    Map<String, String> s = new java.util.LinkedHashMap<>();
                    s.put("name", parts[0]);
                    s.put("load", parts[1]);
                    s.put("active", parts[2]);
                    s.put("sub", parts.length > 3 ? parts[3] : "");
                    s.put("description", parts.length > 4 ? parts[parts.length - 1] : "");
                    list.add(s);
                }
            }
        }
        return list;
    }

    public String getTailscaleIP() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("sh", "-c", "ip addr show tailscale0 2>/dev/null | grep inet | awk '{print $2}' | cut -d'/' -f1");
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return line != null ? line : "No detectado";
        }
    }

    public Map<String, Object> restartService(String name) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sudo", "systemctl", "restart", name);
        Process process = pb.start();
        int exitCode = process.waitFor();
        return Map.of("status", exitCode == 0 ? "ok" : "error", "service", name);
    }

    public Map<String, String> getIp() throws IOException, InterruptedException {
        String local = execCmd("nsenter", "-t", "1", "-n", "hostname", "-I");
        String hostname = execCmd("nsenter", "-t", "1", "-n", "hostname");
        String tailscale = getTailscaleIP();
        return Map.of(
            "local", local.contains(" ") ? local.split(" ")[0] : local,
            "hostname", hostname,
            "tailscale", tailscale.equals("No detectado") ? "" : tailscale
        );
    }

    private String execCmd(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        process.waitFor();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line = reader.readLine();
            return line != null ? line.trim() : "unknown";
        }
    }
}
