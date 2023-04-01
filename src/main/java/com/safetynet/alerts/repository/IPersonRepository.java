package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;

import java.util.Optional;

public interface IPersonRepository {

    Iterable<Person> findAll();

    Optional<Person> findByName(String lastName, String firstName);

    boolean existsByName(String lastName, String firstName);

    void delete(Person person) throws NotFoundException;

    void deleteByName(String lastName, String firstName) throws NotFoundException;

    Person update(String lastName, String firstName, Person person) throws NotFoundException;

    Person save(Person person) throws AlreadyExistsException;
}
