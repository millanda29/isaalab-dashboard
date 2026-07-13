package com.isaalab.dashboard.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DockerService {

    public List<ContainerInfo> getContainers() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "ps", "-a", "--format", "{{.ID}}|{{.Names}}|{{.Image}}|{{.Status}}|{{.Ports}}"
        );
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            List<ContainerInfo> containers = new ArrayList<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] values = line.split("\\|");
                if (values.length >= 5) {
                    containers.add(new ContainerInfo(
                            values[0], values[1], values[2], values[3], values[4]
                    ));
                }
            }

            return containers;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> inspectContainer(String id) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("docker", "inspect", id);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> result = mapper.readValue(json.toString(), List.class);
            if (!result.isEmpty()) {
                return result.get(0);
            }
            return Map.of();
        }
    }

    public List<Map<String, String>> getNetworks() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
                "docker", "network", "ls", "--format", "{{.ID}}|{{.Name}}|{{.Driver}}|{{.Scope}}"
        );
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            List<Map<String, String>> networks = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] v = line.split("\\|");
                if (v.length >= 4) {
                    Map<String, String> net = new java.util.LinkedHashMap<>();
                    net.put("id", v[0]);
                    net.put("name", v[1]);
                    net.put("driver", v[2]);
                    net.put("scope", v[3]);
                    networks.add(net);
                }
            }
            return networks;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> inspectNetwork(String id) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("docker", "network", "inspect", id);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            List<Map<String, Object>> result = mapper.readValue(json.toString(), List.class);
            if (!result.isEmpty()) {
                return result.get(0);
            }
            return Map.of();
        }
    }
}