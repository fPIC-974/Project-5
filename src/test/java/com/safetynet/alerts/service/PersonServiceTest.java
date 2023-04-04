package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalrecordRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private MedicalrecordService medicalrecordService;

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
    public void deleteExistingPersonWithMedicalrecord() throws NotFoundException {

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);
        when(medicalrecordService.existsMedicalrecord(anyString(), anyString())).thenReturn(true);

        personService.deletePersonByName("Doe", "John");

        verify(personRepository).deleteByName(anyString(), anyString());
        verify(medicalrecordService).deleteMedicalrecordByName(anyString(), anyString());
    }

    @Test
    public void deleteExistingPersonWithoutMedicalrecord() throws NotFoundException {

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);
        when(medicalrecordService.existsMedicalrecord(anyString(), anyString())).thenReturn(false);

        personService.deletePersonByName("Doe", "John");

        verify(personRepository).deleteByName(anyString(), anyString());
        verify(medicalrecordService, times(0)).deleteMedicalrecordByName(anyString(), anyString());
    }

    @Test
    public void deleteNonExistingPerson() {
        when(personRepository.existsByName(anyString(), anyString())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personService.deletePersonByName(anyString(), anyString()));

        assertEquals("Person not found", exception.getMessage());
    }

    @Test
    public void saveExistingPerson() {
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> personService.savePerson(person));

        assertEquals(
                "Person already exists", exception.getMessage());
    }

    @Test
    public void saveNonExistingPerson() throws AlreadyExistsException{
        Person person;
        person = new Person();
        person.setLastName("Doe");
        person.setFirstName("John");

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(false);

        personService.savePerson(person);

        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void updateExistingPerson() throws NotFoundException{
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

        when(personRepository.existsByName(anyString(), anyString())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personService.updatePerson("Doe", "John", person));

        assertEquals(
                "Person not found", exception.getMessage());
    }
}