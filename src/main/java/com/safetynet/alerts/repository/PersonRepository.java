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

@Repository
public class PersonRepository {
    private static final Logger logger = LogManager.getLogger("Persons Repository");

    private List<Person> personRepository;

    private final CustomProperties properties;

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

    public Iterable<Person> findAll() {
        return personRepository;
    }

    public Optional<Person> findByName(String lastName, String firstName) {
        return this.personRepository.stream()
                .filter(person -> person.getLastName().equals(lastName) && person.getFirstName().equals(firstName))
                .findFirst();
    }

    public boolean existsByName(String lastName, String firstName) {
        return this.findByName(lastName, firstName).isPresent();
    }

    public void delete(Person person) {
        personRepository.remove(person);
    }

    public void deleteByName(String lastName, String firstName) {
        delete(findByName(lastName, firstName).orElse(null));
    }

    public Person update(String lastName, String firstName, Person person) {
        logger.debug("call: update()");
        // TODO - Use streams
        Person personToUpdate = findByName(lastName, firstName).orElse(null);

        if (personToUpdate != null) {
            personToUpdate.setAddress(person.getAddress());
            personToUpdate.setCity(person.getCity());
            personToUpdate.setZip(person.getZip());
            personToUpdate.setPhone(person.getPhone());
            personToUpdate.setEmail(person.getEmail());

            logger.info("Person updated in repository : " + personToUpdate);
        }
        return personToUpdate;
    }

    /**
     * Saves a person in repository
     * @param person person to be saved
     * @return the person added, or null if firstName/lastName pair already exists
     */
    public Person save(Person person) {
        logger.debug("call: save()");
        personRepository.add(person);
        logger.info("Person added to repository : " + person);
        return person;
    }
}
