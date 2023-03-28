package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    public void getExistingPerson() {
        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(person));

        Person toCheck = personService.getPersonByName("Doe", "John");

        assertNotNull(toCheck);
        assertEquals("Doe", toCheck.getLastName());
        assertEquals("John", toCheck.getFirstName());
    }

    @Test
    public void deleteExistingPerson() {

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);

        personService.deletePersonByName("Doe", "John");

        verify(personRepository).deleteByName(anyString(), anyString());
    }

    @Test
    public void deleteNonExistingPerson() {
        when(personRepository.existsByName(anyString(), anyString())).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personService.deletePersonByName(anyString(), anyString()));

        assertEquals(
                "Person not found", exception.getMessage());
    }

    @Test
    public void saveExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personService.savePerson(person));

        assertEquals(
                "Person already exists", exception.getMessage());
    }

    @Test
    public void saveNonExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(false);

        personService.savePerson(person);

        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void updateExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);

        personService.updatePerson("Doe", "John", person);

        verify(personRepository).update(anyString(), anyString(), any(Person.class));
    }

    @Test
    public void updateNonExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personService.updatePerson("Doe", "John", person));

        assertEquals(
                "Person not found", exception.getMessage());
    }
}