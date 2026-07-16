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

    private static final String[] NSENTER = {"nsenter", "-t", "1", "-m", "-u", "-i", "-n", "-p", "--"};

    public SystemInfo getInfo() throws IOException, InterruptedException {
        double cpu = getCpuUsage();
        long memTotal = getMemoryTotal();
        long memUsed = getMemoryUsed();
        String uptime = getUptime();

        String diskInfo = execHost("df", "-B1", "--output=size,used,avail", "/").split("\n")[1].trim();
        String[] diskParts = diskInfo.split("\\s+");
        long diskTotal = Long.parseLong(diskParts[0]);
        long diskUsed = Long.parseLong(diskParts[1]);
        double diskPercent = diskTotal > 0 ? (diskUsed * 100.0 / diskTotal) : 0;
        double memPercent = memTotal > 0 ? (memUsed * 100.0 / memTotal) : 0;

        return new SystemInfo(
                cpu, memPercent, memUsed, memTotal,
                diskPercent, diskUsed, diskTotal, uptime
        );
    }

    private double getCpuUsage() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "top -bn1 | grep 'Cpu(s)' | awk '{print $2}'");
        if (!out.isEmpty()) return Double.parseDouble(out.replace(",", "."));
        return 0.0;
    }

    private long getMemoryTotal() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "free -b | grep Mem | awk '{print $2}'");
        if (!out.isEmpty()) return Long.parseLong(out.trim());
        return 0;
    }

    private long getMemoryUsed() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "free -b | grep Mem | awk '{print $3}'");
        if (!out.isEmpty()) return Long.parseLong(out.trim());
        return 0;
    }

    private String getUptime() throws IOException, InterruptedException {
        String out = execHost("uptime", "-p");
        return out.replace("up ", "");
    }

    public List<Map<String, String>> getPorts() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "ss -tlnp | tail -n +2");
        List<Map<String, String>> list = new java.util.ArrayList<>();
        for (String line : out.split("\n")) {
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
        return list;
    }

    public List<Map<String, String>> getServices() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "systemctl list-units --type=service --no-pager --no-legend | head -50");
        List<Map<String, String>> list = new java.util.ArrayList<>();
        for (String line : out.split("\n")) {
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
        return list;
    }

    public String getTailscaleIP() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "ip addr show tailscale0 2>/dev/null | grep inet | awk '{print $2}' | cut -d'/' -f1");
        return out.isEmpty() ? "No detectado" : out;
    }

    public Map<String, Object> restartService(String name) throws IOException, InterruptedException {
        String out = execHost("systemctl", "restart", name);
        return Map.of("status", out.contains("error") ? "error" : "ok", "service", name);
    }

    public Map<String, String> getIp() throws IOException, InterruptedException {
        String local = execHost("sh", "-c", "hostname -I | awk '{print $1}'");
        String hostname = execHost("hostname");
        String tailscale = getTailscaleIP();
        return Map.of(
            "local", local,
            "hostname", hostname,
            "tailscale", tailscale.equals("No detectado") ? "" : tailscale
        );
    }

    private String execHost(String... cmd) throws IOException, InterruptedException {
        String[] fullCmd = new String[NSENTER.length + cmd.length];
        System.arraycopy(NSENTER, 0, fullCmd, 0, NSENTER.length);
        System.arraycopy(cmd, 0, fullCmd, NSENTER.length, cmd.length);

        ProcessBuilder pb = new ProcessBuilder(fullCmd);
        Process process = pb.start();
        process.waitFor();

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() > 0) out.append("\n");
                out.append(line);
            }
        }
        return out.toString().trim();
    }
}
