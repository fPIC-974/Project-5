package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonsRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonsService {
    private static final Logger logger = LogManager.getLogger("Persons Service");

    private PersonsRepository personsRepository;

    @Autowired
    public PersonsService(PersonsRepository personsRepository) {
        this.personsRepository = personsRepository;
    }

    public List<Person> getPersons() {
        return (List<Person>) personsRepository.findAll();
    }

    public Person getPersonByName(String lastName, String firstName) {
        return personsRepository.findByName(lastName, firstName).orElse(null);
    }

    public boolean existsPersonByName(String lastName, String firstName) {
        return personsRepository.existsByName(lastName, firstName);
    }

    public void deletePersonByName(String lastName, String firstName) {
        if (existsPersonByName(lastName, firstName)) {
            personsRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }

    public Person savePerson(Person person) {
        if (!existsPersonByName(person.getLastName(), person.getFirstName())) {
            return personsRepository.save(person);
        } else {
            logger.error("Person already exists");
            throw new IllegalStateException("Person already exists");
        }
    }

    public Person updatePerson(String lastName, String firstName, Person person) {
        if (existsPersonByName(lastName, firstName)) {
            return personsRepository.update(lastName, firstName, person);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }
}
