package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {
    private static final Logger logger = LogManager.getLogger("Person Service");

    private PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getPersons() {
        return (List<Person>) personRepository.findAll();
    }

    public Person getPersonByName(String lastName, String firstName) {
        return personRepository.findByName(lastName, firstName).orElse(null);
    }

    public List<Person> getPersonsByCity(String city) {
        return getPersons().stream()
                .filter(p -> p.getCity().equals(city))
                .toList();
    }

    public List<Person> getPersonsByAddress(String address) {
        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    public boolean existsPersonByName(String lastName, String firstName) {
        return personRepository.existsByName(lastName, firstName);
    }

    public void deletePersonByName(String lastName, String firstName) {
        if (existsPersonByName(lastName, firstName)) {
            personRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }

    public Person savePerson(Person person) {
        if (!existsPersonByName(person.getLastName(), person.getFirstName())) {
            return personRepository.save(person);
        } else {
            logger.error("Person already exists");
            throw new IllegalStateException("Person already exists");
        }
    }

    public Person updatePerson(String lastName, String firstName, Person person) {
        if (existsPersonByName(lastName, firstName)) {
            return personRepository.update(lastName, firstName, person);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }
}
