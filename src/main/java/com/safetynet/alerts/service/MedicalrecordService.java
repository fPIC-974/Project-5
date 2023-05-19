package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Functional Medicalrecord object management
 * Business rule : a medicalrecord should exist only if a matching person exists
 * Two possibilities : ignore the medicalrecord creation request (throw exception)
 * or create a user with matching lastname and firstname, and empty other fields
 * The latter could create inconsistencies in app, so the former is chosen
 */
@Service
public class MedicalrecordService implements IMedicalrecordService {
    private static final Logger logger = LogManager.getLogger(MedicalrecordService.class);

    private final IMedicalrecordRepository medicalrecordRepository;

    @Autowired
    public MedicalrecordService(IMedicalrecordRepository medicalrecordRepository) {
        this.medicalrecordRepository = medicalrecordRepository;
    }

    /**
     * Returns a list of Medicalrecord objects
     *
     * @return the list of medicalrecords
     */
    @Override
    public List<Medicalrecord> getMedicalrecords() {
        logger.debug("Method called : getMedicalrecords()");
        return (List<Medicalrecord>) medicalrecordRepository.findAll();
    }

    /**
     * Returns a medicalrecord matching the firstname and lastname provided
     *
     * @param lastName  the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the medicalrecord matching the parameters, or null if not found
     */
    @Override
    public Medicalrecord getMedicalrecord(String lastName, String firstName) {
        logger.debug("Method called : getMedicalrecord(\"" + lastName + "\", \"" + firstName + "\")");
        return medicalrecordRepository.findByName(lastName, firstName).orElse(null);
    }

    /**
     * Deletes a medicalrecord object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     *
     * @param lastName  the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public void deleteMedicalrecordByName(String lastName, String firstName) {
        logger.debug("Method called : deleteMedicalrecordByName(\"" + lastName + "\", \"" + firstName + "\")");

        medicalrecordRepository.delete(lastName, firstName);
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
    public Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) {
        logger.debug("Method called : updateMedicalrecord(\"" + lastName + "\", \"" + firstName + "\", " + medicalrecord + ")");

        return medicalrecordRepository.update(lastName, firstName, medicalrecord);
    }

    /**
     * Get the age of the person matching lastname and firstname parameters
     *
     * @param lastName  the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the age of the person matching parameters
     */
    @Override
    public int getAge(String lastName, String firstName) {
        logger.debug("Method called : getAge(\"" + lastName + "\", \"" + firstName + "\")");
        Medicalrecord medicalrecord = medicalrecordRepository.findByName(lastName, firstName).orElse(null);

        if (medicalrecord != null) {
            LocalDate dob = medicalrecord.getBirthdate();
            return LocalDate.now().compareTo(dob);
        }

        return 0;
    }

    /**
     * Returns whether the person matching the lastname and firstname parameters is minor or not
     *
     * @param lastName  the value of the lastname parameter field
     * @param firstName the value of the firstname parameter field
     * @return true is the person is minor, false otherwise
     */
    @Override
    public boolean isMinor(String lastName, String firstName) {
        logger.debug("Method called : isMinor(\"" + lastName + "\", \"" + firstName + "\")");
        return getAge(lastName, firstName) <= 18;
    }
}
