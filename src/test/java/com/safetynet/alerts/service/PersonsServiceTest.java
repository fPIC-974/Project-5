package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonsRepository;
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
class PersonsServiceTest {

    @Mock
    private PersonsRepository personsRepository;

    @InjectMocks
    private PersonsService personsService;

    @Test
    public void getExistingPerson() {
        Person person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");
        when(personsRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(person));

        Person toCheck = personsService.getPersonByName("Doe", "John");

        assertNotNull(toCheck);
        assertEquals("Doe", toCheck.getLastName());
        assertEquals("John", toCheck.getFirstName());
    }

    @Test
    public void deleteExistingPerson() {

        when(personsRepository.existsByName(anyString(), anyString())).thenReturn(true);

        personsService.deletePersonByName("Doe", "John");

        verify(personsRepository).deleteByName(anyString(), anyString());
    }

    @Test
    public void deleteNonExistingPerson() {
        when(personsRepository.existsByName(anyString(), anyString())).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personsService.deletePersonByName(anyString(), anyString()));

        assertEquals(
                "Person not found", exception.getMessage());
    }

    @Test
    public void saveExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personsRepository.existsByName(anyString(), anyString())).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personsService.savePerson(person));

        assertEquals(
                "Person already exists", exception.getMessage());
    }

    @Test
    public void saveNonExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personsRepository.existsByName(anyString(), anyString())).thenReturn(false);

        personsService.savePerson(person);

        verify(personsRepository).save(any(Person.class));
    }

    @Test
    public void updateExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personsRepository.existsByName(anyString(), anyString())).thenReturn(true);

        personsService.updatePerson("Doe", "John", person);

        verify(personsRepository).update(anyString(), anyString(), any(Person.class));
    }

    @Test
    public void updateNonExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> personsService.updatePerson("Doe", "John", person));

        assertEquals(
                "Person not found", exception.getMessage());
    }
}