package com.safetynet.alerts.repository;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Firestation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository of firestations.
 */
@Repository
public class FirestationRepository implements IFirestationRepository {
    private static final Logger logger = LogManager.getLogger("Firestation Repository");

    private List<Firestation> firestationRepository;

    private final CustomProperties properties;

    /**
     * Constructor
     * @param properties reference to external properties file
     */
    public FirestationRepository(CustomProperties properties) {
        this.properties = properties;
        try {
            File dataSource = ResourceUtils.getFile("classpath:" + this.properties.getDataSource());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(dataSource);
            JsonNode firestationNode = jsonNode.get("firestations");

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Firestation
                    .class);
            this.firestationRepository = objectMapper.readValue(String.valueOf(firestationNode), type);

        } catch (IOException ioException) {
            logger.error(ioException.getMessage(), ioException);
        }
    }

    /**
     * Returns an iterable of Firestations
     * @return the iterable of firestations
     */
    @Override
    public Iterable<Firestation> findAll() {
        return firestationRepository;
    }

    /**
     * Returns a list of elements matching the address provided
     * @param address the value of the field to be matched
     * @return a list of elements matching the parameter
     */
    @Override
    public List<Firestation> findByAddress(String address) {
        return firestationRepository.stream()
                .filter(firestation -> firestation.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of elements matching the station provided
     * @param station the value of the field to be matched
     * @return a list of elements matching the parameter
     */
    @Override
    public List<Firestation> findByStation(int station) {
        return firestationRepository.stream()
                .filter(firestation -> firestation.getStation() == station)
                .collect(Collectors.toList());
    }

    /**
     * Returns a firestation matching address and station parameters
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @return the firestation matching the parameters
     */
    @Override
    public Firestation find(String address, int station) {
        return firestationRepository.stream()
                .filter(firestation -> firestation.getAddress().equals(address) && firestation.getStation() == station)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns whether a firestation with both the given address and station exists
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean exists(String address, int station) {
        return findByAddress(address).stream()
                .anyMatch(firestation -> firestation.getStation() == station && firestation.getAddress().equals(address));
    }

    /**
     * Deletes a given firestation
     * @param firestation the object to be deleted
     */
    @Override
    public void delete(Firestation firestation) {
        firestationRepository.remove(firestation);
    }

    /**
     * Deletes a firestation matching address and station fields
     * Overrides and call delete(Firestation) with the object found
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     */
    @Override
    public void delete(String address, int station) {
        delete(find(address, station));
    }

    /**
     * Updates the firestation matching the address and field parameters
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @param firestation the new firestation object
     * @return the new firestation object, or null if no match
     */
    @Override
    public Firestation update(String address, int station, Firestation firestation) {
        Firestation toUpdate = find(address, station);

        if (toUpdate != null) {
            toUpdate.setAddress(firestation.getAddress());
            toUpdate.setStation(firestation.getStation());
            logger.info("Firestation updated : {address=" + address + ":station=" + station + "} -> " + toUpdate);
        }

        return toUpdate;
    }

    /**
     * Saves a new firestation
     * @param firestation the new firestation to be added
     * @return the firestation added
     */
    @Override
    public Firestation save(Firestation firestation) {
        firestationRepository.add(firestation);
        logger.info("Firestation added : " + firestation);
        return firestation;
    }
}
