package hr.crumbleworks.sensors.controller;

import hr.crumbleworks.sensors.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TemperatureController {

    @Autowired
    private Utils utils;

    @GetMapping("/api/temps")
    public String getTemps() {
        return utils.getCpuTemps();
    }

    @GetMapping("/api/tempsjson")
    public Map<String, Double> getJsonTemps() {
        return utils.getJsonCpuTemps();
    }
}
