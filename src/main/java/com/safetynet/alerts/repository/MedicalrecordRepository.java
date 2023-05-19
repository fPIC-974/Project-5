package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.safetynet.alerts.config.CustomProperties;
import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.InvalidObjectParameterException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Repository of medicalrecords
 */
@Repository
public class MedicalrecordRepository implements IMedicalrecordRepository, IUsable<Medicalrecord> {
    private static final Logger logger = LogManager.getLogger(MedicalrecordRepository.class);

    private List<Medicalrecord> medicalrecordRepository;

    private final CustomProperties properties;


    /**
     * Constructor
     *
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
     *
     * @return the iterable of medicalrecords
     */
    @Override
    public Iterable<Medicalrecord> findAll() {
        logger.debug("Method called : findAll()");
        return medicalrecordRepository;
    }

    /**
     * Returns an Optional of the medicalrecord matching the firstname and lastname provided
     *
     * @param lastName  the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the medicalrecord matching the parameters, or null if not found
     */
    @Override
    public Optional<Medicalrecord> findByName(String lastName, String firstName) {
        logger.debug("Method called : findByName(\"" + lastName + "\", \"" + firstName + "\")");
        return medicalrecordRepository.stream()
                .filter(medicalrecord ->
                        medicalrecord.getLastName().equals(lastName) && medicalrecord.getFirstName().equals(firstName))
                .findFirst();
    }

    /**
     * Deletes a given medicalrecord
     *
     * @param medicalrecord the medicalrecord object to be deleted
     */
    @Override
    public boolean delete(Medicalrecord medicalrecord) {
        logger.debug("Method called : delete(" + medicalrecord + ")");

        if (!medicalrecordRepository.remove(medicalrecord)) {
            logger.error("Medicalrecord not found : " + medicalrecord);
            throw new NotFoundException("Medicalrecord not found");
        }

        logger.info("Deleted : " + medicalrecord);
        return true;
    }

    /**
     * Deletes a medicalrecord object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     *
     * @param lastName  the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public boolean delete(String lastName, String firstName) {
        logger.debug("Method called : deleteByName("
                + lastName + ", " + firstName + ")");

        Medicalrecord foundMedicalrecord = findByName(lastName, firstName).orElse(null);

        if (foundMedicalrecord == null) {
            logger.error("Medicalrecord not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new NotFoundException("Medicalrecord not found");
        }

        return delete(foundMedicalrecord);
    }

    /**
     * Updates the Medicalrecord object matching the firstname and lastname parameters,
     * with values contained in a Medicalrecord object
     *
     * @param lastName      the value of the lastname field to be matched
     * @param firstName     the value of the firstname field to be matched
     * @param medicalrecord the new person object
     * @return the updated Medicalrecord object, or null if no match
     */
    @Override
    public Medicalrecord update(String lastName, String firstName, Medicalrecord medicalrecord) {
        logger.debug("Method called : update("
                + lastName + ", " + firstName + ", " + medicalrecord + ")");

        if (isNotValid(medicalrecord)) {
            logger.error("Invalid parameter : " + medicalrecord);
            throw new AlreadyExistsException("Medicalrecord already exists");
        }

        // stream usage utility ?
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
            throw new NotFoundException("Medicalrecord not found");
        }

        return toUpdate;
    }

    /**
     * Saves a new Medicalrecord object
     *
     * @param medicalrecord the Medicalrecord object to be saved
     * @return the added Medicalrecord object, or null if already exists
     */
    @Override
    public Medicalrecord save(Medicalrecord medicalrecord) {
        logger.debug("Method called : save(" + medicalrecord + ")");

        if (isNotValid(medicalrecord)) {
            logger.error("Invalid parameter : " + medicalrecord);
            throw new InvalidObjectParameterException("Invalid parameter");
        }

        if (findByName(medicalrecord.getLastName(), medicalrecord.getFirstName()).isPresent()) {
            logger.error("Medicalrecord already exists : " + medicalrecord);
            throw new AlreadyExistsException("Medicalrecord already exists");
        }

        medicalrecordRepository.add(medicalrecord);
        logger.info("Medicalrecord added : " + medicalrecord);
        return medicalrecord;
    }

    /**
     * Check whether an object is valid
     * Object is valid if main attributes are not blank (and implicitly not null)
     * @param medicalrecord the object to check
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isNotValid(Medicalrecord medicalrecord) {

        return medicalrecord.getFirstName().isBlank() ||
                medicalrecord.getLastName().isBlank() ||
                medicalrecord.getBirthdate() == null;
    }
}
