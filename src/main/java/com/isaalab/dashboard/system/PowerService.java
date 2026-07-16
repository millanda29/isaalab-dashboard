package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class PowerService {

    public Map<String, Object> monitorOff() throws IOException, InterruptedException {
        return execCmd("sudo", "vbetool", "dpms", "off");
    }

    public Map<String, Object> monitorOn() throws IOException, InterruptedException {
        return execCmd("sudo", "vbetool", "dpms", "on");
    }

    public Map<String, Object> reboot() throws IOException, InterruptedException {
        return execCmd("sudo", "reboot");
    }

    public Map<String, Object> shutdown() throws IOException, InterruptedException {
        return execCmd("sudo", "poweroff");
    }

    private Map<String, Object> execCmd(String... cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();
        int exitCode = process.waitFor();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) out.append(line);
            return Map.of("status", exitCode == 0 ? "ok" : "error", "exitCode", exitCode, "output", out.toString());
        }
    }
}
