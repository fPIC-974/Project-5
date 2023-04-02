package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;

import java.util.List;

public interface IPersonService {
    List<Person> getPersons();

    Person getPersonByName(String lastName, String firstName);

    List<Person> getPersonsByCity(String city);

    List<Person> getPersonsByAddress(String address);

    boolean existsPersonByName(String lastName, String firstName);

    void deletePersonByName(String lastName, String firstName) throws NotFoundException;

    Person savePerson(Person person) throws AlreadyExistsException;

    Person updatePerson(String lastName, String firstName, Person person) throws NotFoundException;
}
