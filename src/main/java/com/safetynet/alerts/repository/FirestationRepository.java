package com.safetynet.alerts.repository;


import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.config.CustomProperties;
import com.safetynet.alerts.exception.InvalidObjectParameterException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository of firestations
 */
@Repository
public class FirestationRepository implements IFirestationRepository, IUsable<Firestation> {
    private static final Logger logger = LogManager.getLogger(FirestationRepository.class);

    private List<Firestation> firestationRepository;

    private final CustomProperties properties;

    /**
     * Constructor
     *
     * @param properties reference to external properties file
     */
    public FirestationRepository(CustomProperties properties) {
        this.properties = properties;
        try {
            InputStream dataSource = new ClassPathResource(this.properties.getDataSource()).getInputStream();

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
     * Returns an iterable of Firestation objects
     *
     * @return the iterable of firestations
     */
    @Override
    public Iterable<Firestation> findAll() {
        logger.debug("Method called : findAll()");
        return firestationRepository;
    }

    /**
     * Returns a list of elements matching the address provided
     *
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
     *
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
     *
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     * @return the firestation matching the parameters
     */
    @Override
    public Optional<Firestation> find(String address, int station) {
        return firestationRepository.stream()
                .filter(firestation -> firestation.getAddress().equals(address) && firestation.getStation() == station)
                .findFirst();
    }

    /**
     * Deletes a given firestation
     *
     * @param firestation the object to be deleted
     */
    @Override
    public boolean delete(Firestation firestation) {
        logger.debug("Method called : delete(" + firestation + ")");

        if (!firestationRepository.remove(firestation)) {
            logger.error("Not found : " + firestation);
            throw new NotFoundException("Firestation not found");
        }

        logger.info("Deleted : " + firestation);
        return true;
    }

    /**
     * Deletes a firestation matching address and station fields
     * Overrides and call delete(Firestation) with the object found
     *
     * @param address the value of the address field to be matched
     * @param station the value of the station field to be matched
     */
    @Override
    public void delete(String address, int station) {
        logger.debug("Method called : delete("
                + address + ", " + station + ")");

        Optional<Firestation> firestation = find(address, station);

        if (firestation.isEmpty()) {
            logger.error("Firestation not found :" +
                    " { address: " + address + ", station: " + station + " }");
            throw new NotFoundException("Firestation not found");
        }

        delete(firestation.get());
    }

    /**
     * Updates the firestation matching the address and field parameters
     *
     * @param address     the value of the address field to be matched
     * @param station     the value of the station field to be matched
     * @param firestation the new firestation object
     * @return the new firestation object, or null if no match
     */
    @Override
    public Firestation update(String address, int station, Firestation firestation) {
        logger.debug("Method called : update("
                + address + ", " + station + ", " + firestation + ")");

        if (isNotValid(firestation)) {
            logger.error("Invalid parameter : " + firestation);
            throw new InvalidObjectParameterException("Invalid parameter");
        }

        Firestation toUpdate = find(address, station).orElse(null);

        if (toUpdate == null) {
            logger.error("Firestation not found :" +
                    " { address: " + address + ", station: " + station + " }");
            throw new NotFoundException("Firestation not found");
        }

        toUpdate.setAddress(firestation.getAddress());
        toUpdate.setStation(firestation.getStation());
        logger.info("Firestation updated :" +
                " {address=" + address + ":station=" + station + "} -> " + toUpdate);

        return toUpdate;
    }

    /**
     * Saves a new firestation
     *
     * @param firestation the new firestation to be added
     * @return the firestation added, or null if already exists
     */
    @Override
    public Firestation save(Firestation firestation) {
        logger.debug("Method called : save(" + firestation + ")");

        if (isNotValid(firestation)) {
            logger.error("Invalid parameter : " + firestation);
            throw new InvalidObjectParameterException("Invalid parameter");
        }

        if (find(firestation.getAddress(), firestation.getStation()).isPresent()) {
            logger.error("Already exists : " + firestation);
            throw new IllegalStateException("Firestation already exists");
        }

        firestationRepository.add(firestation);
        logger.info("Added : " + firestation);
        return firestation;
    }

    @Override
    public boolean isNotValid(Firestation firestation) {
        // No address field ? Empty address field ? station field < 0 ?
        return firestation.getAddress().isBlank() ||
                firestation.getStation() < 0;
    }
}
