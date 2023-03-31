package com.safetynet.alerts.repository;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Repository of persons
 */
@Repository
public class PersonRepository implements IPersonRepository {
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
            File dataSource = ResourceUtils.getFile("classpath:" + this.properties.getDataSource());

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
        return this.personRepository.stream()
                .filter(person -> person.getLastName().equals(lastName) && person.getFirstName().equals(firstName))
                .findFirst();
    }

    /**
     * Returns whether a person with both the given firstname and lastname exists
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     * @return true if the object is found, false otherwise
     */
    @Override
    public boolean existsByName(String lastName, String firstName) {
        return this.findByName(lastName, firstName).isPresent();
    }

    /**
     * Deletes a given person
     * @param person the person object to be deleted
     */
    @Override
    public void delete(Person person) {
        logger.debug("Method called : delete(" + person + ")");

        if(!personRepository.remove(person)) {
            logger.error("Not found : " + person);
            throw new IllegalStateException("Person not found");
        }

        logger.info("Deleted : " + person);
    }

    /**
     * Deletes a person object matching both lastname and firstname fields
     * Overrides and call delete(Person) with the object found
     * @param lastName the value of the lastname field to be matched
     * @param firstName the value of the firstname field to be matched
     */
    @Override
    public void deleteByName(String lastName, String firstName) {
        // TODO - Override delete(Person)
        // TODO - Return bool
        logger.debug("Method called : deleteByName("
                + lastName + ", " + firstName + ")");

        if (existsByName(lastName, firstName)) {
            delete(findByName(lastName, firstName).orElse(null));
        } else {
            logger.error("Person not found :" +
                    " { lastName: " + lastName + ", firstName: " + firstName + " }");
            throw new IllegalStateException("Person not found");
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
    public Person update(String lastName, String firstName, Person person) {
        logger.debug("Method called : update("
                + lastName + ", " + firstName + ", " + person + ")");

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
            throw new IllegalStateException("Person not found");
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

        if (!existsByName(person.getLastName(), person.getFirstName())) {
            personRepository.add(person);
            logger.info("Added : " + person);
            return person;
        } else {
            logger.error("Already exists : " + person);
            throw new IllegalStateException("Person already exists");
        }
    }
}
