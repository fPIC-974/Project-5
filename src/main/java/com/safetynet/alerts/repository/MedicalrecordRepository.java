package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.safetynet.alerts.model.Medicalrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Repository of medicalrecords
 */
@Repository
public class MedicalrecordRepository implements IMedicalrecordRepository {
    private static final Logger logger = LogManager.getLogger(MedicalrecordRepository.class);

    private List<Medicalrecord> medicalrecordRepository;

    private final CustomProperties properties;


    /**
     * Constructor
     * @param properties reference to external properties file
     */
    public MedicalrecordRepository(CustomProperties properties) {
        this.properties = properties;
        try {
            InputStream dataSource = new ClassPathResource(this.properties.getDataSource()).getInputStream();

            ObjectMapper objectMapper = JsonMapper.builder()
                    .findAndAddModules()
                    .build();
            JsonNode jsonNode = objectMapper.readTree(dataSource);
            JsonNode medicalrecordNode = jsonNode.get("medicalrecords");

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Medicalrecord
                    .class);
            this.medicalrecordRepository = objectMapper.readValue(String.valueOf(medicalrecordNode), type);

        } catch (IOException ioException) {
            logger.error(ioException.getMessage(), ioException);
        }
    }

    /**
     * Returns an iterable of Medicalrecord objects
     * @return the iterable of medicalrecords
     */
    @Override
    public Iterable<Medicalrecord> findAll() {
        return medicalrecordRepository;
    }

    /**
     * Returns an Optional of the medicalrecord matching the firstname and lastname provided
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the medicalrecord matching the parameters, or null if not found
     */
    @Override
    public Optional<Medicalrecord> findByName(String lastName, String firstName) {
        return medicalrecordRepository.stream()
                .filter(medicalrecord ->
                        medicalrecord.getLastName().equals(lastName) && medicalrecord.getFirstName().equals(firstName))
                .findFirst();
    }

    /**
     * Returns an Optional of the medicalrecord matching the Medicalrecord object provided
     * @param medicalrecord the object field to be matched
     * @return the medicalrecord matching the parameters, or null if not found
     */
    @Override
    public Optional<Medicalrecord> find(Medicalrecord medicalrecord) {
        return findByName(medicalrecord.getLastName(), medicalrecord.getFirstName());
    }

    /**
     * Returns whether a medicalrecord matching the object provided exists
     * @param medicalrecord the object field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean exists(Medicalrecord medicalrecord) {
        return existsByName(medicalrecord.getLastName(), medicalrecord.getFirstName());
    }

    /**
     * Returns whether a medicalrecord with both the given firstname and lastname exists
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean existsByName(String lastName, String firstName) {
        return findByName(lastName, firstName).isPresent();
    }

    /**
     * Deletes a given medicalrecord
     * @param medicalrecord the medicalrecord object to be deleted
     */
    @Override
    public void delete(Medicalrecord medicalrecord) {
        logger.debug("Method called : delete(" + medicalrecord + ")");

        if(!medicalrecordRepository.remove(medicalrecord)) {
            logger.error("Not found : " + medicalrecord);
            throw new IllegalStateException("Medicalrecord not found");
        }

        logger.info("Deleted : " + medicalrecord);
    }

    /**
     * Deletes a medicalrecord object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public void deleteByName(String lastName, String firstName) {
        // TODO - Return bool
        logger.debug("Method called : deleteByName("
                + lastName + ", " + firstName + ")");

        if (existsByName(lastName, firstName)) {
            delete(findByName(lastName, firstName).orElse(null));
        } else {
            logger.error("Medicalrecord not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    /**
     * Updates the Medicalrecord object matching the firstname and lastname parameters,
     * with values contained in a Medicalrecord object
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @param medicalrecord the new person object
     * @return the updated Medicalrecord object, or null if no match
     */
    @Override
    public Medicalrecord update(String lastName, String firstName, Medicalrecord medicalrecord) {
        logger.debug("Method called : update("
                + lastName + ", " + firstName + ", " + medicalrecord + ")");

        Medicalrecord toUpdate = findByName(lastName, firstName).stream()
                .peek(m -> {
                    m.setBirthdate(medicalrecord.getBirthdate());
                    m.setMedications(medicalrecord.getMedications());
                    m.setAllergies(medicalrecord.getAllergies());
                })
                .findFirst()
                .orElse(null);

        if (toUpdate != null) {
            logger.info("Medicalrecord updated :" +
                    " {lastName=" + lastName + ":firstName=" + firstName + "} -> " + toUpdate);
        } else {
            logger.error("Medicalrecord not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new IllegalStateException("Medicalrecord not found");
        }

        return toUpdate;
    }

    /**
     * Saves a new Medicalrecord object
     * @param medicalrecord the Medicalrecord object to be saved
     * @return the added Medicalrecord object, or null if already exists
     */
    @Override
    public Medicalrecord save(Medicalrecord medicalrecord) {
        logger.debug("Method called : save(" + medicalrecord + ")");

        if (!exists(medicalrecord)) {
            medicalrecordRepository.add(medicalrecord);
            logger.info("Added : " + medicalrecord);
            return medicalrecord;
        } else {
            logger.error("Already exists : " + medicalrecord);
            throw new IllegalStateException("Medicalrecord already exists");
        }
    }
}
