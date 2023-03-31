package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.safetynet.alerts.model.Medicalrecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class MedicalrecordRepository implements IMedicalrecordRepository {
    private static final Logger logger = LogManager.getLogger("Medicalrecord Repository");

    private List<Medicalrecord> medicalrecordRepository;

    private final CustomProperties properties;

    public MedicalrecordRepository(CustomProperties properties) {
        this.properties = properties;
        try {
            File dataSource = ResourceUtils.getFile("classpath:" + this.properties.getDataSource());

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

    @Override
    public Iterable<Medicalrecord> findAll() {
        return medicalrecordRepository;
    }

    @Override
    public Optional<Medicalrecord> findByName(String lastName, String firstName) {
        return medicalrecordRepository.stream()
                .filter(medicalrecord ->
                        medicalrecord.getLastName().equals(lastName) && medicalrecord.getFirstName().equals(firstName))
                .findFirst();
    }

    @Override
    public boolean existsByName(String lastName, String firstName) {
        return findByName(lastName, firstName).isPresent();
    }

    @Override
    public void delete(Medicalrecord medicalrecord) {
        logger.debug("call: delete()");
        medicalrecordRepository.remove(medicalrecord);
        logger.info("Medicalrecord deleted from repository : " + medicalrecord);
    }

    @Override
    public void deleteByName(String lastName, String firstName) {
        delete(findByName(lastName, firstName).orElse(null));
    }

    @Override
    public Medicalrecord update(String lastName, String firstName, Medicalrecord medicalrecord) {
        logger.debug("call: update()");
        return findByName(lastName, firstName).stream()
                .peek(m -> {
                    m.setBirthdate(medicalrecord.getBirthdate());
                    m.setMedications(medicalrecord.getMedications());
                    m.setAllergies(medicalrecord.getAllergies());

                    logger.info("Medicalrecord updated in repository : " + m);
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public Medicalrecord save(Medicalrecord medicalrecord) {
        logger.debug("call: save()");
        medicalrecordRepository.add(medicalrecord);
        logger.info("Medicalrecord added to repository : " + medicalrecord);
        return medicalrecord;
    }
}
