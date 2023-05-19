package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Person;

import java.util.Optional;

public interface IPersonRepository {

    Iterable<Person> findAll();

    Optional<Person> findByName(String lastName, String firstName);

    boolean delete(Person person);

    boolean delete(String lastName, String firstName);

    Person update(String lastName, String firstName, Person person);

    Person save(Person person);
}
