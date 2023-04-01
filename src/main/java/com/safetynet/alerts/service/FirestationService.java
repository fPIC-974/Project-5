package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlertsException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.IFirestationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Functional Firestation object management
 */
@Service
public class FirestationService implements IFirestationService {
    private static final Logger logger = LogManager.getLogger(FirestationService.class);

    private final IFirestationRepository firestationRepository;

    @Autowired
    public FirestationService(IFirestationRepository firestationRepository) {
        this.firestationRepository = firestationRepository;
    }

    /**
     * Returns a list of Firestation objects
     * @return the list of firestations
     */
    @Override
    public List<Firestation> getFirestations() {
        return (List<Firestation>) firestationRepository.findAll();
    }

    /**
     * Returns a list of elements matching the address provided
     * @param address the value of the field to be matched
     * @return a list of elements matching the parameter
     */
    @Override
    public List<Firestation> getFirestationByAddress(String address) {
        return firestationRepository.findByAddress(address);
    }

    /**
     * Returns a list of elements matching the station provided
     * @param station the value of the field to be matched
     * @return a list of elements matching the parameter
     */
    @Override
    public List<Firestation> getFirestationByStation(int station) {
        return firestationRepository.findByStation(station);
    }

    /**
     * Returns a firestation matching address and station parameters
     *
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @return the firestation matching the parameters
     */
    @Override
    public Firestation getFirestation(String address, int station) {
        return firestationRepository.find(address, station).orElse(null);
    }

    /**
     * Returns whether a firestation with both the given address and station exists
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean existsFirestation(String address, int station) {
        return firestationRepository.exists(address, station);
    }

    /**
     * Deletes a given firestation
     * @param firestation the object to be deleted
     */
    @Override
    public void deleteFirestation(Firestation firestation) {
        if (existsFirestation(firestation.getAddress(), firestation.getStation())) {
            firestationRepository.delete(firestation);
        } else {
            logger.error("Firestation not found: " + firestation);
            throw new IllegalStateException("Firestation not found");
        }
    }

    /**
     * Deletes a firestation matching address and station fields
     * Overrides and call delete(Firestation) with the object found
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     */
    @Override
    public void deleteFirestation(String address, int station) {
        if (existsFirestation(address, station)) {
            firestationRepository.delete(address, station);
        } else {
            logger.error("Firestation not found: " + address + ":" + station);
            throw new IllegalStateException("Firestation not found");
        }
    }

    /**
     * Updates the firestation matching the address and field parameters
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @param firestation the new firestation object
     * @return the new firestation object, or null if no match
     */
    @Override
    public Firestation updateFirestation(String address, int station, Firestation firestation) {
        if (existsFirestation(address, station)) {
            return firestationRepository.update(address, station, firestation);
        } else {
            logger.error("Firestation not found: " + address + ":" + station);
            throw new IllegalStateException("Firestation not found");
        }
    }

    /**
     * Saves a new firestation
     * @param firestation the new firestation to be added
     * @return the firestation added, or null if already exists
     */
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
