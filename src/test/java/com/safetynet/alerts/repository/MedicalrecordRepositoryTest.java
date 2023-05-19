package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedicalrecordRepositoryTest {

    @Autowired
    private MedicalrecordRepository medicalrecordRepository;

    @Test
    public void shouldGetExistingMedicalrecord() {
        Medicalrecord toCheck = medicalrecordRepository.findByName("Boyd", "Felicia").orElse(null);

        assertNotNull(toCheck);
    }

    @Test
    public void shouldNotGetMissingMedicalrecord() {
        Medicalrecord toCheck = medicalrecordRepository.findByName("WWalker", "Reginold").orElse(null);

        assertNull(toCheck);
    }

    @Test
    public void shouldDeleteExistingMedicalrecord() {
        Medicalrecord medicalrecord = medicalrecordRepository.findByName("Peters", "Ron").orElse(null);

        assertTrue(medicalrecordRepository.delete(medicalrecord));
    }

    @Test
    public void shouldNotDeleteMissingMedicalrecord() {
        Medicalrecord medicalrecord = medicalrecordRepository.findByName("WWalker", "Reginold").orElse(null);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> medicalrecordRepository.delete(medicalrecord));

        assertTrue(nfe.getMessage().contains("Medicalrecord not found"));
    }

    @Test
    public void shouldDeleteExistingMedicalrecordByName() {
        assertTrue(medicalrecordRepository.delete("Walker", "Reginold"));
    }

    @Test
    public void shouldNotDeleteMissingMedicalrecordByName() {

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> medicalrecordRepository.delete("WWalker", "Reginold"));

        assertTrue(nfe.getMessage().contains("Medicalrecord not found"));
    }

    @Test
    public void shouldUpdateExistingMedicalrecord() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Walker");
        medicalrecord.setFirstName("Reginold");
        medicalrecord.setBirthdate(LocalDate.now());
        medicalrecord.setMedications(new ArrayList<>(Collections.singleton("aznol:200mg")));
        medicalrecord.setAllergies(new ArrayList<>(Collections.singleton("nillacilan")));

        Medicalrecord toCheck = medicalrecordRepository.update("Walker", "Reginold", medicalrecord);

        assertNotNull(toCheck);
    }

    @Test
    public void shouldNotUpdateMissingMedicalrecord() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Walker");
        medicalrecord.setFirstName("Reginold");
        medicalrecord.setBirthdate(LocalDate.now());
        medicalrecord.setMedications(new ArrayList<>(Collections.singleton("aznol:200mg")));
        medicalrecord.setAllergies(new ArrayList<>(Collections.singleton("nillacilan")));

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> medicalrecordRepository.update("WWalker", "Reginold", medicalrecord));

        assertTrue(nfe.getMessage().contains("Medicalrecord not found"));
    }

    @Test
    public void shouldSaveMissingMedicalrecord() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("John");
        medicalrecord.setLastName("Doe");
        medicalrecord.setBirthdate(LocalDate.now());

        assertNotNull(medicalrecordRepository.save(medicalrecord));
    }

    @Test
    public void shouldNotSaveExistingMedicalrecord() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("John");
        medicalrecord.setLastName("Boyd");
        medicalrecord.setBirthdate(LocalDate.now());

        AlreadyExistsException aee = assertThrows(AlreadyExistsException.class,
                () -> medicalrecordRepository.save(medicalrecord));

        assertTrue(aee.getMessage().contains("Medicalrecord already exists"));
    }
}