package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.IFirestationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
        return firestationService.getFirestations();
    }

    @GetMapping("/addr/{address}")
    public List<Firestation> getFirestationsByAddress(@PathVariable String address) {
        return firestationService.getFirestationByAddress(address);
    }

    @GetMapping("/stat/{station}")
    public List<Firestation> getFirestationsByStation(@PathVariable int station) {
        return firestationService.getFirestationByStation(station);
    }

    @GetMapping("/{address}/{station}")
    public Firestation getFirestation(@PathVariable String address, @PathVariable int station) {
        return firestationService.getFirestation(address, station);
    }

    @PostMapping
    public Firestation saveFirestation(@RequestBody Firestation firestation) throws AlreadyExistsException {
        return firestationService.saveFirestation(firestation);
    }

    @PutMapping("/{address}/{station}")
    public Firestation updateFirestation(@PathVariable String address,
                                         @PathVariable int station,
                                         @RequestBody Firestation firestation) throws NotFoundException {

        return firestationService.updateFirestation(address, station, firestation);
    }

    @DeleteMapping("/{address}/{station}")
    public void deleteFirestation(@PathVariable String address, @PathVariable int station) throws NotFoundException {
        firestationService.deleteFirestation(address, station);
    }
}
