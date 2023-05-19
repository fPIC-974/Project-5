package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Medicalrecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FirestationRepositoryTest {

    @Autowired
    private FirestationRepository firestationRepository;

    @Test
    public void shouldGetExistingFirestationByAddress() {
        List<Firestation> toCheck = firestationRepository.findByAddress("644 Gershwin Cir");

        assertEquals(1, toCheck.size());
        assertFalse(toCheck.isEmpty());
    }

    @Test
    public void shouldGetExistingFirestationByStation() {
        List<Firestation> toCheck = firestationRepository.findByStation(1);

        assertFalse(toCheck.isEmpty());
        assertEquals(3, toCheck.size());
    }

    @Test
    public void shouldNotGetMissingFirestationByAddress() {
        List<Firestation> toCheck = firestationRepository.findByAddress("6644 Gershwin Cir");

        assertTrue(toCheck.isEmpty());
    }

    @Test
    public void shouldNotGetMissingFirestationByStation() {
        List<Firestation> toCheck = firestationRepository.findByStation(11);

        assertTrue(toCheck.isEmpty());
    }

    @Test
    public void shouldGetExistingFirestation() {
        Optional<Firestation> toCheck = firestationRepository.find("644 Gershwin Cir", 1);

        assertTrue(toCheck.isPresent());
    }

    @Test
    public void shouldNotGetMissingFirestation() {
        Optional<Firestation> toCheck = firestationRepository.find("6644 Gershwin Cir", 1);

        assertTrue(toCheck.isEmpty());
    }

    @Test
    public void shouldDeleteExistingFirestation() {
        assertDoesNotThrow(() -> firestationRepository.delete("644 Gershwin Cir", 1));
    }

    @Test
    public void shouldDeleteExistingFirestationByEntity() {
        Firestation firestation = firestationRepository.find("748 Townings Dr", 3).orElse(null);

        boolean toCheck = firestationRepository.delete(firestation);

        assertTrue(toCheck);
    }

    @Test
    public void shouldNotDeleteMissingFirestation() {

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> firestationRepository.delete("644 Gershwin Cir", 11));

        assertTrue(nfe.getMessage().contains("Firestation not found"));
    }

    @Test
    public void shouldNotDeleteMissingFirestationByEntity() {
        Firestation firestation = firestationRepository.find("644 Gershwin Cir", 11).orElse(null);

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> firestationRepository.delete(firestation));

        assertTrue(nfe.getMessage().contains("Firestation not found"));
    }
}