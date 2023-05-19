package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import com.safetynet.alerts.repository.IPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CrossService implements ICrossService {
    private static final Logger logger = LogManager.getLogger(CrossService.class);

    private final IPersonRepository personRepository;
    private final IMedicalrecordRepository medicalrecordRepository;

    public CrossService(IPersonRepository personRepository, IMedicalrecordRepository medicalrecordRepository) {
        this.medicalrecordRepository = medicalrecordRepository;
        this.personRepository = personRepository;
    }


    /**
     * Save a Medicalrecord object
     * Should only add medicalrecord if associated person exists
     * @param medicalrecord the medicalrecord to add
     * @return the saved medicalrecord
     */
    @Override
    public Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord) {
        logger.debug("Method called : saveMedicalrecord(" + medicalrecord + ")");

        if (personRepository.findByName(medicalrecord.getLastName(), medicalrecord.getFirstName()).isEmpty()) {
            logger.error("Person not found :" +
                    " { lastName: " + medicalrecord.getLastName() + ", firstName: " + medicalrecord.getFirstName() + " }");
            throw new NotFoundException("Person not found");
        }

        return medicalrecordRepository.save(medicalrecord);
    }

    /**
     * Delete a person from Person repository
     * Should also delete the associated medicalrecord
     * @param lastName the lastname of the person
     * @param firstName the firstname of the person
     */
    @Override
    public void deletePersonByName(String lastName, String firstName) {
        logger.debug("Method called : deletePersonByName(\"" + lastName + "\", \"" + firstName + "\")");

        try {
            medicalrecordRepository.delete(lastName, firstName);
        } catch (NotFoundException nfe) {
            logger.debug("No medicalrecord found for " + lastName + " " + firstName);
        } finally {
            personRepository.delete(lastName, firstName);
        }
    }
}
