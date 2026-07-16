package com.isaalab.dashboard.system;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;
    private final PowerService powerService;
    private final ProcessService processService;

    @GetMapping("/ip")
    public ResponseEntity<?> ip() {
        try {
            return ResponseEntity.ok(systemService.getIp());
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("local", "unknown"));
        }
    }

    @GetMapping
    public ResponseEntity<?> info() {
        try {
            return ResponseEntity.ok(systemService.getInfo());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ports")
    public ResponseEntity<?> ports() {
        try {
            return ResponseEntity.ok(systemService.getPorts());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/services")
    public ResponseEntity<?> services() {
        try {
            return ResponseEntity.ok(systemService.getServices());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/services/{name}/restart")
    public ResponseEntity<?> restartService(@PathVariable String name) {
        try {
            return ResponseEntity.ok(systemService.restartService(name));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/monitor/off")
    public ResponseEntity<?> monitorOff() {
        try {
            return ResponseEntity.ok(powerService.monitorOff());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/monitor/on")
    public ResponseEntity<?> monitorOn() {
        try {
            return ResponseEntity.ok(powerService.monitorOn());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/power/reboot")
    public ResponseEntity<?> reboot() {
        try {
            return ResponseEntity.ok(powerService.reboot());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/power/shutdown")
    public ResponseEntity<?> shutdown() {
        try {
            return ResponseEntity.ok(powerService.shutdown());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/processes")
    public ResponseEntity<?> processes() {
        try {
            return ResponseEntity.ok(processService.getProcesses());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/processes/{pid}/kill")
    public ResponseEntity<?> killProcess(@PathVariable String pid) {
        try {
            return ResponseEntity.ok(processService.killProcess(pid));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}