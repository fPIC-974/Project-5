package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import com.safetynet.alerts.repository.IPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public PersonService(IPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Get all persons in repository
     * @return a list of Person objects
     */
    @Override
    public List<Person> getPersons() {
        logger.debug("Method called : getPersons()");
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
        logger.debug("Method called : getPersonByName(\"" + lastName + "\", \"" + firstName + "\")");
        return personRepository.findByName(lastName, firstName).orElse(null);
    }

    /**
     * Get persons in the matching city
     * @param city the city field to be matched
     * @return a list of Person objects matching the parameter
     */
    @Override
    public List<Person> getPersonsByCity(String city) {
        logger.debug("Method called : getPersonsByCity(\"" + city + "\")");
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
        logger.debug("Method called : getPersonsByAddress(\"" + address + "\")");

        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    /**
     * Saves a new person object
     * @param person person to be saved
     * @return the added person object, or null if already exists
     */
    @Override
    public Person savePerson(Person person) {
        logger.debug("Method called : savePerson(" + person + ")");

        return personRepository.save(person);
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
    public Person updatePerson(String lastName, String firstName, Person person) {
        logger.debug("Method called : updatePerson(\"" + lastName + "\", \"" + firstName + "\", " + person + ")");

        return personRepository.update(lastName, firstName, person);
    }
}
