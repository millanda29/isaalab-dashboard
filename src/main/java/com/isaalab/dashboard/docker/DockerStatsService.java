package com.isaalab.dashboard.docker;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class DockerStatsService {

    public List<Map<String, Object>> getStats() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "stats", "--no-stream", "--format",
                "{{.Name}}|{{.CPUPerc}}|{{.MemUsage}}|{{.MemPerc}}|{{.NetIO}}|{{.BlockIO}}"
        );
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            List<Map<String, Object>> stats = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\|");
                if (values.length >= 6) {
                    Map<String, Object> stat = new LinkedHashMap<>();
                    stat.put("name", values[0]);
                    stat.put("cpu", values[1]);
                    stat.put("memory", values[2]);
                    stat.put("memoryPerc", values[3]);
                    stat.put("netIO", values[4]);
                    stat.put("blockIO", values[5]);
                    stats.add(stat);
                }
            }

            return stats;
        }
    }
}
