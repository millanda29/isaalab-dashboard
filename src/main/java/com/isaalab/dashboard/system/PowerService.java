package com.isaalab.dashboard.system;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Service
public class PowerService {

    private static final String[] NSENTER = {"nsenter", "-t", "1", "-m", "-u", "-i", "-n", "-p", "--"};

    public Map<String, Object> monitorOff() throws IOException, InterruptedException {
        return execHost("vbetool", "dpms", "off");
    }

    public Map<String, Object> monitorOn() throws IOException, InterruptedException {
        return execHost("vbetool", "dpms", "on");
    }

    public Map<String, Object> reboot() throws IOException, InterruptedException {
        return execHost("reboot");
    }

    public Map<String, Object> shutdown() throws IOException, InterruptedException {
        return execHost("poweroff");
    }

    private Map<String, Object> execHost(String... cmd) throws IOException, InterruptedException {
        String[] fullCmd = new String[NSENTER.length + cmd.length];
        System.arraycopy(NSENTER, 0, fullCmd, 0, NSENTER.length);
        System.arraycopy(cmd, 0, fullCmd, NSENTER.length, cmd.length);

        ProcessBuilder pb = new ProcessBuilder(fullCmd);
        Process process = pb.start();
        int exitCode = process.waitFor();

        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());
        String output = stdout.isEmpty() ? stderr : stdout;

        return Map.of(
            "status", exitCode == 0 ? "ok" : "error",
            "exitCode", exitCode,
            "output", output
        );
    }

    private String readStream(java.io.InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (out.length() > 0) out.append("\n");
                out.append(line);
            }
            return out.toString();
        }
    }
}
