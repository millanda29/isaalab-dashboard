package com.isaalab.dashboard.docker;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DockerLogsService {

    public List<String> getLogs(String containerId, int tail) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "logs", "--tail", String.valueOf(tail), containerId
        );
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            List<String> logs = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                logs.add(line);
            }
            return logs;
        }
    }

    public List<String> getLogs(String containerId) throws IOException {
        return getLogs(containerId, 100);
    }
}
