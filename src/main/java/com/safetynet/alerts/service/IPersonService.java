package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;

import java.util.List;

public interface IPersonService {
    List<Person> getPersons();

    Person getPersonByName(String lastName, String firstName);

    List<Person> getPersonsByCity(String city);

    List<Person> getPersonsByAddress(String address);

    boolean existsPersonByName(String lastName, String firstName);

    void deletePersonByName(String lastName, String firstName);

    Person savePerson(Person person);

    Person updatePerson(String lastName, String firstName, Person person);
}
