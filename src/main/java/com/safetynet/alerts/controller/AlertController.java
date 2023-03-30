package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.AlertService;
import com.safetynet.alerts.service.PersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/")
public class AlertController {
    private static final Logger logger = LogManager.getLogger("Alert Controller");

    private final AlertService alertService;

    @Autowired
    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping("/firestation")
    public Map<String, Object> firestation(@RequestParam int stationNumber) {
        return alertService.getPersonsCovered(stationNumber);
    }

    @GetMapping("/childAlert")
    public Map<String, Object> childAlert(@RequestParam String address) {
        return alertService.childAlert(address);
    }

    @GetMapping("/phoneAlert")
    public Set<String> phoneAlert(@RequestParam int firestation) {
        return alertService.phoneAlert(firestation);
    }

    @GetMapping("/fire")
    public Map<String, Object> fire(@RequestParam String address) {
        return alertService.fire(address);
    }

    @GetMapping("/personInfo")
    public JsonNode personInfo(@RequestParam String lastName,
                               @RequestParam String firstName) {
        return alertService.personInfo(lastName, firstName);
    }

    @GetMapping("/communityEmail")
    public Set<String> communityEmail(@RequestParam String city) {
        return alertService.communityEmail(city);
    }

    @GetMapping("/flood")
    public Map<String, List<ObjectNode>> flood(@RequestParam int[] stations) {
        return alertService.flood(stations);
    }
}
