package hr.crumbleworks.sensors.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class Utils {

    @Value("${system.sensors.command}")
    private String sensorsCommand;

    @Value("${system.sensors.package}")
    private String packageRegex;

    @Value("${system.sensors.core}")
    private String coreRegex;

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

}
