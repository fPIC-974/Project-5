package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import com.safetynet.alerts.repository.IPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Functional Medicalrecord object management
 *
 * Business rule : a medicalrecord should exist only if a matching person exists
 * Two possibilities : ignore the medicalrecord creation request (throw exception)
 * or create a user with matching lastname and firstname, and empty other fields
 * The latter could create inconsistencies in app, so the former is chosen
 */
@Service
public class MedicalrecordService implements IMedicalrecordService {
    private static final Logger logger = LogManager.getLogger(MedicalrecordService.class);

    private final IMedicalrecordRepository medicalrecordRepository;

    private final IPersonRepository personRepository;

    @Autowired
    public MedicalrecordService(IMedicalrecordRepository medicalrecordRepository, IPersonRepository personRepository) {
        this.medicalrecordRepository = medicalrecordRepository;
        this.personRepository = personRepository;
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
    public void deleteMedicalrecordByName(String lastName, String firstName) throws NotFoundException{
        logger.debug("Method called : deleteMedicalrecordByName(" + lastName + ", " + firstName + ")");
        if (existsMedicalrecord(lastName, firstName)) {
            medicalrecordRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new NotFoundException("Medicalrecord not found");
        }
    }

    /**
     * Saves a new Medicalrecord object
     * @param medicalrecord the Medicalrecord object to be saved
     * @return the added Medicalrecord object, or null if already exists
     */
    @Override
    public Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord) throws AlreadyExistsException, NotFoundException{
        logger.debug("Method called : saveMedicalRecord(" + medicalrecord + ")");
        // If the person does not exist, the medical record can not be created
        if (!personRepository.existsByName(medicalrecord.getLastName(), medicalrecord.getFirstName())) {
            logger.error("Person not found : "
                    + medicalrecord.getFirstName() + ":"
                    + medicalrecord.getLastName());
            throw new NotFoundException("Person not found");
        }

        if (!existsMedicalrecord(medicalrecord.getLastName(), medicalrecord.getFirstName())) {
            return medicalrecordRepository.save(medicalrecord);
        } else {
            logger.error("Medicalrecord already exists : "
                    + medicalrecord.getFirstName() + ":"
                    + medicalrecord.getLastName());
            throw new AlreadyExistsException("Medicalrecord already exists");
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
    public Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) throws NotFoundException {
        logger.debug("Method called : updateMedicalrecord(" + lastName + ", " + firstName + ", " + medicalrecord + ")");
        if (existsMedicalrecord(lastName, firstName)) {
            return medicalrecordRepository.update(lastName, firstName, medicalrecord);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new NotFoundException("Medicalrecord not found");
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
