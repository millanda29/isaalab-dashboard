package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class ProcessService {

    public List<Map<String, String>> getProcesses() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "ps", "aux", "--sort=-%cpu", "--no-headers"
        );
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            List<Map<String, String>> list = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
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
    }

    public Map<String, Object> killProcess(String pid) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("kill", pid);
        Process process = pb.start();
        int exitCode = process.waitFor();
        return Map.of("status", exitCode == 0 ? "ok" : "error", "pid", pid);
    }
}
