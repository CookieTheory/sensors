package hr.crumbleworks.sensors.controller;

import hr.crumbleworks.sensors.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class PowerController {

    @Autowired
    private Utils utils;

    @GetMapping("/api/watts")
    public double getTemps() throws IOException, InterruptedException {
        return utils.getCpuWatts();
    }

    @GetMapping("/api/wattsjson")
    public Map<String, Double> getJsonTemps() throws IOException, InterruptedException {
        return utils.getJsonCpuWatts();
    }

}
