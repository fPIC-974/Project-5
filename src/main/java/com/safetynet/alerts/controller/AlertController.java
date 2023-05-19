package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.safetynet.alerts.service.IAlertService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/")
public class AlertController {
    private static final Logger logger = LogManager.getLogger(AlertController.class);

    private final IAlertService alertService;

    @Autowired
    public AlertController(IAlertService alertService) {
        this.alertService = alertService;
    }

/*    @GetMapping("/firestation")
    public Map<String, Object> firestation(@RequestParam int stationNumber) {
        logger.info("GET REQUEST : /firestation?stationNumber=" + stationNumber);
        Map<String, Object> result = alertService.firestation(stationNumber);

        logger.info("RESPONSE : " + result);

        return result;
    }*/

    @GetMapping("/firestation")
    public ResponseEntity<?> firestation(@RequestParam int stationNumber) throws IOException {
        logger.info("GET REQUEST : /firestation?stationNumber=" + stationNumber);
        Map<String, Object> result = alertService.firestation(stationNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (((List<?>) result.get("persons")).isEmpty()) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

/*    @GetMapping("/childAlert")
    public Map<String, Object> childAlert(@RequestParam String address) {
        logger.info("GET REQUEST : /childAlert?address=" + address);
        Map<String, Object> result = alertService.childAlert(address);

        logger.info("RESPONSE : " + result);
        return result;
    }*/

    @GetMapping("/childAlert")
    public ResponseEntity<?> childAlert(@RequestParam String address) {
        logger.info("GET REQUEST : /childAlert?address=" + address);
        Map<String, Object> result = alertService.childAlert(address);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (((List<?>) result.get("children")).isEmpty()) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.NOT_FOUND);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

    @GetMapping("/phoneAlert")
    public Set<String> phoneAlert(@RequestParam int firestation) {
        logger.info("GET REQUEST : /phoneAlert?firestation=" + firestation);
        Set<String> result = alertService.phoneAlert(firestation);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/fire")
    public Map<String, Object> fire(@RequestParam String address) {
        logger.info("GET REQUEST : /fire?address=" + address);
        Map<String, Object> result = alertService.fire(address);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/personInfo")
    public JsonNode personInfo(@RequestParam String lastName,
                               @RequestParam String firstName) {
        logger.info("GET REQUEST : /personInfo?lastName=" + lastName + "&firstName=" + firstName);
        JsonNode result = alertService.personInfo(lastName, firstName);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/communityEmail")
    public Set<String> communityEmail(@RequestParam String city) {
        logger.info("GET REQUEST : /communityEmail?city=" + city);
        Set<String> result = alertService.communityEmail(city);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/flood")
    public Map<String, List<ObjectNode>> flood(@RequestParam int[] stations) {
        logger.info("GET REQUEST : /flood?stations=" + Arrays.toString(stations));
        Map<String, List<ObjectNode>> result = alertService.flood(stations);

        logger.info("RESPONSE : " + result);
        return result;
    }
}
