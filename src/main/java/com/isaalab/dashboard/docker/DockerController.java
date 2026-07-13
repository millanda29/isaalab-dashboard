package com.isaalab.dashboard.docker;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docker")
@RequiredArgsConstructor
public class DockerController {

    private final DockerService dockerService;
    private final DockerStatsService dockerStatsService;
    private final DockerLogsService dockerLogsService;

    @GetMapping("/containers")
    public ResponseEntity<?> containers() {
        try {
            return ResponseEntity.ok(dockerService.getContainers());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener contenedores: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        try {
            return ResponseEntity.ok(dockerStatsService.getStats());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener stats: " + e.getMessage()));
        }
    }

    @GetMapping("/logs/{id}")
    public ResponseEntity<?> logs(@PathVariable String id,
                                  @RequestParam(defaultValue = "100") int tail) {
        try {
            return ResponseEntity.ok(dockerLogsService.getLogs(id, tail));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener logs: " + e.getMessage()));
        }
    }

    @PostMapping("/restart/{id}")
    public ResponseEntity<?> restart(@PathVariable String id) {
        return execDockerCommand("restart", id);
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<?> start(@PathVariable String id) {
        return execDockerCommand("start", id);
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<?> stop(@PathVariable String id) {
        return execDockerCommand("stop", id);
    }

    @GetMapping("/inspect/{id}")
    public ResponseEntity<?> inspect(@PathVariable String id) {
        try {
            return ResponseEntity.ok(dockerService.inspectContainer(id));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al inspeccionar contenedor: " + e.getMessage()));
        }
    }

    private ResponseEntity<Map<String, String>> execDockerCommand(String action, String id) {
        try {
            ProcessBuilder pb = new ProcessBuilder("docker", action, id);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return ResponseEntity.ok(Map.of("status", "ok", "container", id, "action", action));
            }
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "container", id, "action", action));
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}