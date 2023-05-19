package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;

import java.util.List;

public interface IPersonService {
    List<Person> getPersons();

    Person getPersonByName(String lastName, String firstName);

    List<Person> getPersonsByCity(String city);

    List<Person> getPersonsByAddress(String address);

    Person savePerson(Person person);

    Person updatePerson(String lastName, String firstName, Person person);
}
