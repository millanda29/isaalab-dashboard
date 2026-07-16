package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ProcessService {

    private static final String[] NSENTER = {"nsenter", "-t", "1", "-m", "-u", "-i", "-n", "-p", "--"};

    public List<Map<String, String>> getProcesses() throws IOException, InterruptedException {
        String out = execHost("sh", "-c", "ps aux --sort=-%cpu --no-headers");
        List<Map<String, String>> list = new ArrayList<>();

        for (String line : out.split("\n")) {
            String[] parts = line.trim().split("\\s+", 11);
            if (parts.length >= 11) {
                Map<String, String> p = new LinkedHashMap<>();
                p.put("user", parts[0]);
                p.put("pid", parts[1]);
                p.put("cpu", parts[2]);
                p.put("mem", parts[3]);
                p.put("vsz", parts[4]);
                p.put("rss", parts[5]);
                p.put("tty", parts[6]);
                p.put("stat", parts[7]);
                p.put("start", parts[8]);
                p.put("time", parts[9]);
                p.put("command", parts[10]);
                list.add(p);
            }
        }
        return list;
    }

    public Map<String, Object> killProcess(String pid) throws IOException, InterruptedException {
        String out = execHost("kill", pid);
        boolean ok = out.isEmpty();
        return Map.of("status", ok ? "ok" : "error", "pid", pid);
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
