package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.MedicalrecordService;
import com.safetynet.alerts.service.PersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalrecord")
public class MedicalrecordController {
    private static final Logger logger = LogManager.getLogger("Medicalrecord Controller");

    private final MedicalrecordService medicalrecordService;

    @Autowired
    public MedicalrecordController(MedicalrecordService medicalrecordService) {
        this.medicalrecordService = medicalrecordService;
    }

    @GetMapping("/all")
    public List<Medicalrecord> getMedicalrecords() {
        return medicalrecordService.getMedicalrecords();
    }

    @GetMapping("/{lastName}/{firstName}")
    public Medicalrecord getMedicalrecord(@PathVariable String lastName, @PathVariable String firstName) {
        return medicalrecordService.getMedicalrecord(lastName, firstName);
    }

    @PostMapping
    public Medicalrecord saveMedicalrecord(@RequestBody Medicalrecord medicalrecord) {
        return medicalrecordService.saveMedicalrecord(medicalrecord);
    }

    @PutMapping("/{lastName}/{firstName}")
    public Medicalrecord updateMedicalrecord(@PathVariable String lastName,
                                             @PathVariable String firstName,
                                             @RequestBody Medicalrecord medicalrecord) {
        return medicalrecordService.updateMedicalrecord(lastName, firstName, medicalrecord);
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public void deleteMedicalRecord(@PathVariable String lastName,
                                             @PathVariable String firstName) {
        medicalrecordService.deleteMedicalrecordByName(lastName, firstName);
    }
}
