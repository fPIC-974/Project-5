package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirestationServiceTest {

    @Mock
    private FirestationRepository firestationRepository;

    @InjectMocks
    private FirestationService firestationService;

    @Test
    public void getExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(10);

        when(firestationRepository.find(anyString(), anyInt())).thenReturn(Optional.of(firestation));

        Firestation toCheck = firestationService.getFirestation("test address", 10);

        assertNotNull(toCheck);
        assertEquals("test address", toCheck.getAddress());
        assertEquals(10, toCheck.getStation());
    }

    @Test
    public void deleteExistingFirestation() throws NotFoundException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(10);
        when(firestationRepository.exists(anyString(), anyInt())).thenReturn(true);

        firestationService.deleteFirestation(firestation);

        verify(firestationRepository).delete(any(Firestation.class));
    }

    @Test
    public void deleteExistingFirestationByAddress() throws NotFoundException {
        when(firestationRepository.exists(anyString(), anyInt())).thenReturn(true);

        firestationService.deleteFirestation(anyString(), anyInt());

        verify(firestationRepository).delete(anyString(), anyInt());
    }

    @Test
    public void deleteNonExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(10);
        when(firestationRepository.exists(anyString(), anyInt())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> firestationService.deleteFirestation(firestation));

        assertEquals("Firestation not found", exception.getMessage());
    }

    @Test
    public void deleteNonExistingFirestationByAddress() {
        when(firestationRepository.exists(anyString(), anyInt())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> firestationService.deleteFirestation(anyString(), anyInt()));

        assertEquals("Firestation not found", exception.getMessage());
    }

    @Test
    public void updateExistingFirestation() throws NotFoundException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.exists("test address", 10)).thenReturn(true);

        firestationService.updateFirestation("test address", 10, firestation);

        verify(firestationRepository).update(anyString(), anyInt(), any(Firestation.class));
    }

    @Test
    public void updateNonExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.exists("test address", 10)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> firestationService.updateFirestation("test address", 10, firestation));

        assertEquals("Firestation not found", exception.getMessage());
    }

    @Test
    public void saveExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.exists("test address", 15)).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> firestationService.saveFirestation(firestation));

        assertEquals("Firestation already exists", exception.getMessage());
    }

    @Test
    public void saveNonExistingFirestation() throws AlreadyExistsException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.exists("test address", 15)).thenReturn(false);

        firestationService.saveFirestation(firestation);

        verify(firestationRepository).save(any(Firestation.class));
    }
}