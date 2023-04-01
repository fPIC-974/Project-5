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
     * @return the list of medicalrecords
     */
    @Override
    public List<Medicalrecord> getMedicalrecords() {
        return (List<Medicalrecord>) medicalrecordRepository.findAll();
    }

    /**
     * Returns a medicalrecord matching the firstname and lastname provided
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the medicalrecord matching the parameters, or null if not found
     */
    @Override
    public Medicalrecord getMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.findByName(lastName, firstName).orElse(null);
    }

    /**
     * Returns whether a medicalrecord with both the given firstname and lastname exists
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean existsMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.existsByName(lastName, firstName);
    }

    /**
     * Deletes a medicalrecord object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public void deleteMedicalrecordByName(String lastName, String firstName) {
        if (existsMedicalrecord(lastName, firstName)) {
            medicalrecordRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    /**
     * Saves a new Medicalrecord object
     * @param medicalrecord the Medicalrecord object to be saved
     * @return the added Medicalrecord object, or null if already exists
     */
    @Override
    public Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord) {
        if (!existsMedicalrecord(medicalrecord.getLastName(), medicalrecord.getFirstName())) {
            return medicalrecordRepository.save(medicalrecord);
        } else {
            logger.error("Medicalrecord already exists : "
                    + medicalrecord.getFirstName() + ":"
                    + medicalrecord.getLastName());
            throw new IllegalStateException("Medicalrecord already exists");
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
    public Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) {
        if (existsMedicalrecord(lastName, firstName)) {
            return medicalrecordRepository.update(lastName, firstName, medicalrecord);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    /**
     * Get the age of the person matching lastname and firstname parameters
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the age of the person matching parameters
     */
    @Override
    public int getAge(String lastName, String firstName) {
        LocalDate dob = getMedicalrecord(lastName, firstName).getBirthdate();
        return LocalDate.now().compareTo(dob);
    }

    /**
     * Returns whether the person matching the lastname and firstname parameters is minor or not
     * @param lastName the value of the lastname parameter field
     * @param firstName the value of the firstname parameter field
     * @return true is the person is minor, false otherwise
     */
    @Override
    public boolean isMinor(String lastName, String firstName) {
        return getAge(lastName, firstName) <= 18;
    }
}
