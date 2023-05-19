package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.rmi.AlreadyBoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PersonRepositoryTest {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void userShouldExist() {
        Person toCheck = personRepository.findByName("Picard", "Fred").orElse(null);

        assertNotNull(toCheck);
    }

    @Test
    public void userShouldNotExist() {
        Person toCheck = personRepository.findByName("Dracip", "Fred").orElse(null);

        assertNull(toCheck);
    }

    @Test
    public void shouldDeleteExistingPerson() {
        Person person = personRepository.findByName("Stelzer", "Brian").orElse(null);

        boolean toCheck = personRepository.delete(person);

        assertTrue(toCheck);
    }

    @Test
    public void shouldNotDeleteMissingPerson() {
        Person person = personRepository.findByName("Picaard", "Fred").orElse(null);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> personRepository.delete(person));

        assertTrue(nfe.getMessage().contains("Person not found"));

    }

    @Test
    public void shouldDeleteExistingPersonByName() {
        boolean toCheck = personRepository.delete("Boyd", "Felicia");

        assertTrue(toCheck);
    }

    @Test
    public void shouldNotDeleteMissingPersonByName() {
        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> personRepository.delete("Picaard", "Fred"));

        assertTrue(nfe.getMessage().contains("Person not found"));
    }

    @Test
    public void shouldUpdateExistingPerson() {
        Person person = new Person();
        person.setFirstName("Fred");
        person.setLastName("Picard");
        person.setAddress("Any street");
        person.setCity("Nowhere");
        person.setZip(11111);
        person.setPhone("XXX-XXXXX");
        person.setEmail("jdoe@mail.com");

        Person toCheck = personRepository.update("Picard", "Fred", person);

        assertNotNull(toCheck);
    }

    @Test
    public void shouldNotUpdateMissingPerson() {
        Person person = new Person();
        person.setFirstName("Fred");
        person.setLastName("Dracip");
        person.setAddress("Any street");
        person.setCity("Nowhere");
        person.setZip(11111);
        person.setPhone("XXX-XXXXX");
        person.setEmail("jdoe@mail.com");

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> personRepository.update("Dracip", "Fred", person));

        assertTrue(nfe.getMessage().contains("Person not found"));
    }

    @Test
    public void shouldSaveMissingPerson() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("Any street");
        person.setCity("Nowhere");
        person.setZip(11111);
        person.setPhone("XXX-XXXXX");
        person.setEmail("jdoe@mail.com");

        Person toCheck = personRepository.save(person);

        assertNotNull(toCheck);
    }

    @Test
    public void shouldNotSaveExistingPerson() {
        Person person = new Person();
        person.setFirstName("Fred");
        person.setLastName("Picard");
        person.setAddress("Any street");
        person.setCity("Nowhere");
        person.setZip(11111);
        person.setPhone("XXX-XXXXX");
        person.setEmail("fred@mail.com");

        AlreadyExistsException aee = assertThrows(AlreadyExistsException.class,
                () -> personRepository.save(person));

        assertTrue(aee.getMessage().contains("Person already exists"));
    }
}