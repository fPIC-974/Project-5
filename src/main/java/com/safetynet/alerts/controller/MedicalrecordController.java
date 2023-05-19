package com.safetynet.alerts.controller;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.service.ICrossService;
import com.safetynet.alerts.service.IMedicalrecordService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalrecordController {
    private static final Logger logger = LogManager.getLogger(MedicalrecordController.class);

    private final IMedicalrecordService medicalrecordService;
    private final ICrossService crossService;

    @Autowired
    public MedicalrecordController(ICrossService crossService, IMedicalrecordService medicalrecordService) {
        this.medicalrecordService = medicalrecordService;
        this.crossService = crossService;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getMedicalrecords() {
        logger.info("GET REQUEST : /medicalrecord/all");
        List<Medicalrecord> result = medicalrecordService.getMedicalrecords();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (result.isEmpty()) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }


        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result, headers, HttpStatus.OK);
    }

    @GetMapping("/{lastName}/{firstName}")
    public ResponseEntity<Object> getMedicalrecord(@PathVariable String lastName, @PathVariable String firstName) {
        logger.info("GET REQUEST : /medicalrecord/" + lastName + "/" + firstName);
        Medicalrecord result = medicalrecordService.getMedicalrecord(lastName,firstName);

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
    public Medicalrecord saveMedicalrecord(@RequestBody Medicalrecord medicalrecord) {
        logger.info("POST REQUEST : /medicalrecord + Body = " + medicalrecord);

        Medicalrecord result = crossService.saveMedicalrecord(medicalrecord);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @PutMapping("/{lastName}/{firstName}")
    public Medicalrecord updateMedicalrecord(@PathVariable String lastName,
                                             @PathVariable String firstName,
                                             @RequestBody Medicalrecord medicalrecord) {
        logger.info("PUT REQUEST : /medicalrecord/" + lastName + "/" + firstName + " + Body = " + medicalrecord);

        Medicalrecord result = medicalrecordService.updateMedicalrecord(lastName, firstName, medicalrecord);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public void deleteMedicalRecord(@PathVariable String lastName,
                                             @PathVariable String firstName) {
        logger.info("DELETE REQUEST : /medicalrecord/" + lastName + "/" + firstName);

        medicalrecordService.deleteMedicalrecordByName(lastName, firstName);
    }
}
