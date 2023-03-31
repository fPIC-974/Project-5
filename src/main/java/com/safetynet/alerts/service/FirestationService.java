package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.IFirestationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FirestationService implements IFirestationService {
    private static final Logger logger = LogManager.getLogger("Firestations Service");

    private final IFirestationRepository firestationRepository;

    @Autowired
    public FirestationService(IFirestationRepository firestationRepository) {
        this.firestationRepository = firestationRepository;
    }

    @Override
    public List<Firestation> getFirestations() {
        return (List<Firestation>) firestationRepository.findAll();
    }

    @Override
    public List<Firestation> getFirestationByAddress(String address) {
        return firestationRepository.findByAddress(address);
    }

    @Override
    public List<Firestation> getFirestationByStation(int station) {
        return firestationRepository.findByStation(station);
    }

    @Override
    public Firestation getFirestation(String address, int station) {
        return firestationRepository.find(address, station);
    }

    @Override
    public boolean existsFirestation(String address, int station) {
        return firestationRepository.exists(address, station);
    }

    @Override
    public void deleteFirestation(Firestation firestation) {
        if (existsFirestation(firestation.getAddress(), firestation.getStation())) {
            firestationRepository.delete(firestation);
        } else {
            logger.error("Firestation not found: " + firestation);
            throw new IllegalStateException("Firestation not found");
        }
    }

    @Override
    public void deleteFirestation(String address, int station) {
        if (existsFirestation(address, station)) {
            firestationRepository.delete(address, station);
        } else {
            logger.error("Firestation not found: " + address + ":" + station);
            throw new IllegalStateException("Firestation not found");
        }
    }

    @Override
    public Firestation updateFirestation(String address, int station, Firestation firestation) {
        if (existsFirestation(address, station)) {
            return firestationRepository.update(address, station, firestation);
        } else {
            logger.error("Firestation not found: " + address + ":" + station);
            throw new IllegalStateException("Firestation not found");
        }
    }

    @Override
    public Firestation saveFirestation(Firestation firestation) {
        if (!existsFirestation(firestation.getAddress(), firestation.getStation())) {
            return firestationRepository.save(firestation);
        } else {
            logger.error("Firestation already exists: " + firestation);
            throw new IllegalStateException("Firestation already exists");
        }
    }
}
