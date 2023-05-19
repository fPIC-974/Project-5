package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.config.CustomProperties;
import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.InvalidObjectParameterException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * Repository of persons
 */
@Repository
public class PersonRepository implements IPersonRepository, IUsable<Person> {
    private static final Logger logger = LogManager.getLogger(PersonRepository.class);

    private List<Person> personRepository;

    private final CustomProperties properties;

    /**
     * Constructor
     * @param properties reference to external properties file
     */
    public PersonRepository(CustomProperties properties) {
        this.properties = properties;

        try {
            InputStream dataSource = new ClassPathResource(this.properties.getDataSource()).getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(dataSource);
            JsonNode personNode = jsonNode.get("persons");

            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Person
                    .class);
            this.personRepository = objectMapper.readValue(String.valueOf(personNode), type);

        } catch (IOException ioException) {
            logger.error(ioException.getMessage(), ioException);
        }
    }

    /**
     * Returns an iterable of Person objects
     * @return the iterable of persons
     */
    @Override
    public Iterable<Person> findAll() {
        logger.debug("Method called : findAll()");
        return personRepository;
    }

    /**
     * Returns an Optional of the person matching the firstname and lastname provided
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return the person matching the parameters, or null if not found
     */
    @Override
    public Optional<Person> findByName(String lastName, String firstName) {
        logger.debug("Method called : findByName(\"" + lastName + "\", \"" + firstName + "\")");
        return this.personRepository.stream()
                .filter(person -> person.getLastName().equals(lastName) && person.getFirstName().equals(firstName))
                .findFirst();
    }

    /**
     * Deletes a given person
     * @param person the person object to be deleted
     */
    @Override
    public boolean delete(Person person) {
        logger.debug("Method called : delete(" + person + ")");

        if(!personRepository.remove(person)) {
            logger.error("Person not found : " + person);
            throw new NotFoundException("Person not found");
        }

        logger.info("Deleted : " + person);

        return true;
    }

    /**
     * Deletes a person object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public boolean delete(String lastName, String firstName) {
        logger.debug("Method called : deleteByName(\""
                + lastName + "\", \"" + firstName + "\")");

        Person foundPerson = findByName(lastName, firstName).orElse(null);

        if (foundPerson == null) {
            logger.error("Person not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new NotFoundException("Person not found");
        }

        return delete(foundPerson);
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
    public Person update(String lastName, String firstName, Person person) {
        logger.debug("Method called : update("
                + lastName + ", " + firstName + ", " + person + ")");

        if (isNotValid(person)) {
            logger.error("Invalid parameter : " + person);
            throw new InvalidObjectParameterException("Invalid parameter");
        }

        Person toUpdate = findByName(lastName, firstName).orElse(null);

        if (toUpdate != null) {
            toUpdate.setAddress(person.getAddress());
            toUpdate.setCity(person.getCity());
            toUpdate.setZip(person.getZip());
            toUpdate.setPhone(person.getPhone());
            toUpdate.setEmail(person.getEmail());

            logger.info("Person updated :" +
                    " {lastName=" + lastName + ":firstName=" + firstName + "} -> " + toUpdate);
        } else {
            logger.error("Person not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new NotFoundException("Person not found");
        }

        return toUpdate;
    }

    /**
     * Saves a new person object
     * @param person person to be saved
     * @return the added person object, or null if already exists
     */
    @Override
    public Person save(Person person) {
        logger.debug("Method called : save(" + person + ")");

        if (isNotValid(person)) {
            logger.error("Invalid parameter : " + person);
            throw new InvalidObjectParameterException("Invalid parameter");
        }

        Optional<Person> existingPerson = findByName(person.getLastName(), person.getFirstName());

        if (existingPerson.isPresent()) {
            logger.error("Person already exists : " + existingPerson);
            throw new AlreadyExistsException("Person already exists");
        }

        personRepository.add(person);
        logger.info("Person added : " + person);
        return person;
    }

    /**
     * Check whether an object is valid
     * Object is valid if main attributes are not blank (and implicitly not null)
     * @param person the object to check
     * @return true if valid, false otherwise
     */
    @Override
    public boolean isNotValid(Person person) {
        return person.getFirstName().isBlank() ||
                person.getLastName().isBlank() ||
                person.getAddress().isBlank() ||
                person.getCity().isBlank() ||
                person.getZip() < 0 ||
                person.getPhone().isBlank() ||
                person.getEmail().isBlank();
    }
}
