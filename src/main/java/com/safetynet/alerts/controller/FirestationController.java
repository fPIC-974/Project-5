package com.safetynet.alerts.controller;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.IFirestationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firestation")
public class FirestationController {
    private static final Logger logger = LogManager.getLogger(FirestationController.class);

    private final IFirestationService firestationService;

    @Autowired
    public FirestationController(IFirestationService firestationService) {
        this.firestationService = firestationService;
    }

    @GetMapping("/all")
    public List<Firestation> getFirestations() {
        logger.info("GET REQUEST : /firestation/all");
        List<Firestation> result = firestationService.getFirestations();

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/addr/{address}")
    public ResponseEntity<?> getFirestationsByAddress(@PathVariable String address) {
        logger.info("GET REQUEST : /firestation/addr/" + address);
        List<Firestation> result = firestationService.getFirestationByAddress(address);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (result == null) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

    @GetMapping("/stat/{station}")
    public ResponseEntity<?> getFirestationsByStation(@PathVariable int station) {
        logger.info("GET REQUEST : /firestation/stat/" + station);
        List<Firestation> result = firestationService.getFirestationByStation(station);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (result == null) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

    @GetMapping("/{address}/{station}")
    public ResponseEntity<?> getFirestation(@PathVariable String address, @PathVariable int station) {
        logger.info("GET REQUEST : /firestation/" + address + "/" + station);
        Firestation result = firestationService.getFirestation(address, station);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (result == null) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

    @PostMapping
    public Firestation saveFirestation(@RequestBody Firestation firestation) {
        logger.info("POST REQUEST : /firestation + Body=" + firestation);

        Firestation result = firestationService.saveFirestation(firestation);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @PutMapping("/{address}/{station}")
    public Firestation updateFirestation(@PathVariable String address,
                                         @PathVariable int station,
                                         @RequestBody Firestation firestation) {

        logger.info("PUT REQUEST : /firestation/" + address + "/" + station + " + Body = " + firestation);

        Firestation result = firestationService.updateFirestation(address, station, firestation);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @DeleteMapping("/{address}/{station}")
    public void deleteFirestation(@PathVariable String address, @PathVariable int station) {
        logger.info("DELETE REQUEST : /firestation/" + address + "/" + station);

        firestationService.deleteFirestation(address, station);
    }
}
