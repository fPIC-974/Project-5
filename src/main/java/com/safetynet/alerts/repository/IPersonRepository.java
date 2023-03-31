package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;

import java.util.Optional;

public interface IPersonRepository {

    Iterable<Person> findAll();

    Optional<Person> findByName(String lastName, String firstName);

    boolean existsByName(String lastName, String firstName);

    void delete(Person person);

    void deleteByName(String lastName, String firstName);

    Person update(String lastName, String firstName, Person person);

    Person save(Person person);
}
