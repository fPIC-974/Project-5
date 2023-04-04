package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import com.safetynet.alerts.repository.IPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Functional Person object management
 * Business rule : a medicalrecord should exist only if a matching person exists
 * A delete request should therefore imply the deletion of the corresponding medicalrecord
 */
@Service
public class PersonService implements IPersonService {
    private static final Logger logger = LogManager.getLogger(PersonService.class);

    private final IPersonRepository personRepository;

    private final IMedicalrecordService medicalrecordService;

    @Autowired
    public PersonService(IPersonRepository personRepository, @Lazy IMedicalrecordService medicalrecordService) {
        this.personRepository = personRepository;
        this.medicalrecordService = medicalrecordService;
    }

    /**
     * Get all persons in repository
     * @return a list of Person objects
     */
    @Override
    public List<Person> getPersons() {
        return (List<Person>) personRepository.findAll();
    }

    /**
     * Get a person matching lastname and firstname parameters
     * @param lastName the lastname field to be matched
     * @param firstName the firstname field to be matched
     * @return the Person object matching the parameters, null if not found
     */
    @Override
    public Person getPersonByName(String lastName, String firstName) {
        return personRepository.findByName(lastName, firstName).orElse(null);
    }

    /**
     * Get persons in the matching city
     * @param city the city field to be matched
     * @return a list of Person objects matching the parameter
     */
    @Override
    public List<Person> getPersonsByCity(String city) {
        return getPersons().stream()
                .filter(p -> p.getCity().equals(city))
                .toList();
    }

    /**
     * Get persons matching the address
     * @param address the address field to be matched
     * @return a list of Person objects matching the parameter
     */
    @Override
    public List<Person> getPersonsByAddress(String address) {
        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Returns whether a person matching parameters exists in repository
     * @param lastName the lastname field to be matched
     * @param firstName the firstname field to be matched
     * @return true if the person is found, false otherwise
     */
    @Override
    public boolean existsPersonByName(String lastName, String firstName) {
        return personRepository.existsByName(lastName, firstName);
    }

    /**
     * Deletes a person object matching both lastname and firstname fields
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public void deletePersonByName(String lastName, String firstName) throws NotFoundException {
        logger.debug("Method called : deletePersonByName(" + lastName + ", " + firstName + ")");
        if (existsPersonByName(lastName, firstName)) {
            personRepository.deleteByName(lastName, firstName);
            if (medicalrecordService.existsMedicalrecord(lastName, firstName)) {
                medicalrecordService.deleteMedicalrecordByName(lastName, firstName);
            }
        } else {
            logger.error("Person not found : "
                    + lastName + ":"
                    + firstName);
            throw new NotFoundException("Person not found");
        }
    }

    /**
     * Saves a new person object
     * @param person person to be saved
     * @return the added person object, or null if already exists
     */
    @Override
    public Person savePerson(Person person) throws AlreadyExistsException {
        logger.debug("Method called : savePerson(" + person + ")");
        if (!existsPersonByName(person.getLastName(), person.getFirstName())) {
            return personRepository.save(person);
        } else {
            logger.error("Person already exists : "
                    + person.getLastName() + ":"
                    + person.getFirstName());
            throw new AlreadyExistsException("Person already exists");
        }
    }

    /**
     * Updates the person object matching the firstname and lastname parameters,
     * with values contained in a person object
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @param person the new person object
     * @return the updated person object, or null if no match
     */
    @Override
    public Person updatePerson(String lastName, String firstName, Person person) throws NotFoundException {
        logger.debug("Method called : updatePerson(" + lastName + ", " + firstName + ", " + person + ")");
        if (existsPersonByName(lastName, firstName)) {
            return personRepository.update(lastName, firstName, person);
        } else {
            logger.error("Person not found : "
                    + lastName + ":"
                    + firstName);
            throw new NotFoundException("Person not found");
        }
    }
}
