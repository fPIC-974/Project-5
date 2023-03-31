package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.IPersonRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService implements IPersonService {
    private static final Logger logger = LogManager.getLogger("Person Service");

    private final IPersonRepository personRepository;

    @Autowired
    public PersonService(IPersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<Person> getPersons() {
        return (List<Person>) personRepository.findAll();
    }

    @Override
    public Person getPersonByName(String lastName, String firstName) {
        return personRepository.findByName(lastName, firstName).orElse(null);
    }

    @Override
    public List<Person> getPersonsByCity(String city) {
        return getPersons().stream()
                .filter(p -> p.getCity().equals(city))
                .toList();
    }

    @Override
    public List<Person> getPersonsByAddress(String address) {
        return getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsPersonByName(String lastName, String firstName) {
        return personRepository.existsByName(lastName, firstName);
    }

    @Override
    public void deletePersonByName(String lastName, String firstName) {
        if (existsPersonByName(lastName, firstName)) {
            personRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }

    @Override
    public Person savePerson(Person person) {
        if (!existsPersonByName(person.getLastName(), person.getFirstName())) {
            return personRepository.save(person);
        } else {
            logger.error("Person already exists");
            throw new IllegalStateException("Person already exists");
        }
    }

    @Override
    public Person updatePerson(String lastName, String firstName, Person person) {
        if (existsPersonByName(lastName, firstName)) {
            return personRepository.update(lastName, firstName, person);
        } else {
            logger.error("Person not found");
            throw new IllegalStateException("Person not found");
        }
    }
}
