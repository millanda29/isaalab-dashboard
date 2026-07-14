package com.isaalab.dashboard.system;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping("/ip")
    public ResponseEntity<?> ip() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            return ResponseEntity.ok(Map.of("ip", ip.getHostAddress(), "hostname", ip.getHostName()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("ip", "unknown"));
        }
    }

    @GetMapping
    public ResponseEntity<?> info() {
        try {
            return ResponseEntity.ok(systemService.getInfo());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener info del sistema: " + e.getMessage()));
        }
    }
}
