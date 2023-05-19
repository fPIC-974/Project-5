package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.Disabled;
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
    public void getNonExistingPerson() {
        when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.ofNullable(null));

        Person toCheck = personService.getPersonByName("Doe", "John");

        assertNull(toCheck);
    }

    /*@Test
    public void deleteNonExistingPerson() {
        when(personRepository.delete(anyString(), anyString())).thenThrow(new NotFoundException("Person not found"));

        NotFoundException nfe = assertThrows(NotFoundException.class, () -> personService.deletePersonByName(anyString(), anyString()));

        assertTrue(nfe.getMessage().contains("Person not found"));
    }*/

    /*@Test
    public void deleteExistingPerson() {
        assertDoesNotThrow(() -> personService.deletePersonByName(anyString(), anyString()));

        verify(personRepository, times(1)).delete(anyString(), anyString());
    }*/

    @Test
    public void saveExistingPerson() {
        when(personRepository.save(any(Person.class))).thenThrow(new AlreadyExistsException("Person already exists"));

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> personService.savePerson(new Person()));

        assertEquals("Person already exists", exception.getMessage());
    }

    @Test
    public void saveNonExistingPerson() throws AlreadyExistsException{
        assertDoesNotThrow(() -> personService.savePerson(new Person()));

        verify(personRepository).save(any(Person.class));
    }

    @Test
    public void updateExistingPerson() throws NotFoundException{
        when(personRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(new Person()));

        assertDoesNotThrow(() -> personService.updatePerson("Doe", "John", new Person()));

        verify(personRepository).update(anyString(), anyString(), any(Person.class));
        reset(personRepository);
    }

    @Test
    public void updateNonExistingPerson() {
        when(personRepository.update(anyString(), anyString(), any(Person.class))).thenThrow(new NotFoundException("Person not found"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personService.updatePerson("Doe", "John", new Person()));

        assertEquals(
                "Person not found", exception.getMessage());
    }
}