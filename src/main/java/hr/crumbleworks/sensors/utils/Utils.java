package hr.crumbleworks.sensors.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class Utils {
    private static final String ENERGY_PATH = "/sys/class/powercap/intel-rapl:0/energy_uj";
    private static final String NETWORK_COMMAND = " RX1=$(cat /proc/net/dev | grep $INTERFACE | awk '{print $2}'); " +
            "TX1=$(cat /proc/net/dev | grep $INTERFACE | awk '{print $10}'); sleep 1; RX2=$(cat /proc/net/dev | grep" +
            " $INTERFACE | awk '{print $2}'); TX2=$(cat /proc/net/dev | grep $INTERFACE | awk '{print $10}'); RX=$((" +
            " (RX2 - RX1)*8/1000 )); TX=$(( (TX2 - TX1)*8/1000 )); echo \"Down:${RX} | Up:${TX}\"";

    @Value("${system.sensors.command}")
    private String sensorsCommand;

    @Value("${system.sensors.package}")
    private String packageRegex;

    @Value("${system.sensors.core}")
    private String coreRegex;

    @Value("${system.sensors.network}")
    private String networkInterface;

    public String getCpuTemps() {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", sensorsCommand);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return output.toString();
    }

    public Map<String, Double> getJsonCpuTemps() {
        Map<String, Double> tempMap = new HashMap<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", sensorsCommand);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(packageRegex)) {
                    String temp = line.split(":")[1].trim().split("°")[0].replace("+", "");
                    tempMap.put("package", Double.parseDouble(temp));
                } else if (line.startsWith(coreRegex)) {
                    String coreName = line.split(":")[0].toLowerCase().replace(" ", "_");
                    String temp = line.split(":")[1].trim().split("°")[0].replace("+", "");
                    tempMap.put(coreName, Double.parseDouble(temp));
                }
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read sensors output");
        }
        return tempMap;
    }

    public double getCpuWatts() throws IOException, InterruptedException {
        long e1 = readEnergy();
        Thread.sleep(1000);
        long e2 = readEnergy();

        long deltaEnergy = e2 - e1;
        if (deltaEnergy < 0) {
            deltaEnergy += Long.MAX_VALUE;
        }

        double watts = deltaEnergy / 1_000_000.0;
        return watts;
    }

    private long readEnergy() throws IOException {
        String content = Files.readString(Paths.get(ENERGY_PATH)).trim();
        return Long.parseLong(content);
    }

    public Map<String, Double> getJsonCpuWatts() throws IOException, InterruptedException {
        Map<String, Double> wattMap = new HashMap<>();
        wattMap.put("watts", getCpuWatts());
        return wattMap;
    }

    public String getNetworkSpeed() {
        StringBuilder output = new StringBuilder();
        try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", networkInterface + NETWORK_COMMAND);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
        return output.toString().trim();
    }

    public Map<String, Double> getJsonNetworkSpeed() {
        Map<String, Double> networkMap = new HashMap<>();
        String line = getNetworkSpeed();
        if (line.startsWith("Down")) {
            String[] parts = line.split("\\|");
            for (String part : parts) {
                String[] keyValue = part.trim().split(":");
                if (keyValue.length == 2) {
                    String direction = keyValue[0].trim().toLowerCase();
                    double speed = Double.parseDouble(keyValue[1].trim());
                    networkMap.put(direction, speed);
                }
            }
        }
        return networkMap;
    }

}
