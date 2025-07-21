package hr.crumbleworks.sensors.controller;

import hr.crumbleworks.sensors.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class NetworkController {

    @Autowired
    private Utils utils;

    @GetMapping("/api/network")
    public String getNetwork() {
        return utils.getNetworkSpeed();
    }

    @GetMapping("/api/networkjson")
    public Map<String, Double> getJsonNetwork() {
        return utils.getJsonNetworkSpeed();
    }
}
