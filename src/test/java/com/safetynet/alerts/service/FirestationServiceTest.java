package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.InvalidObjectParameterException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.repository.FirestationRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InvalidObjectException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
        when(firestationRepository.find(anyString(), anyInt())).thenReturn(Optional.of(firestation));

        firestationService.deleteFirestation(firestation);

        verify(firestationRepository).delete(any(Firestation.class));
        reset(firestationRepository);
    }

    @Test
    public void deleteExistingFirestationByAddress() throws NotFoundException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(10);
        when(firestationRepository.find(anyString(), anyInt())).thenReturn(Optional.of(firestation));

        firestationService.deleteFirestation(firestation.getAddress(), firestation.getStation());

        verify(firestationRepository).delete(anyString(), anyInt());
        reset(firestationRepository);
    }

    @Test
    public void deleteNonExistingFirestation() {
        when(firestationRepository.delete(anyString(), anyInt())).thenThrow(new NotFoundException("Firestation not found"));
        when(firestationRepository.find(anyString(), anyInt())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> firestationService.deleteFirestation("Whatever", 22));

        assertEquals("Firestation not found", exception.getMessage());

        reset(firestationRepository);
    }

    @Test
    public void deleteNonExistingFirestationByAddress() {
        when(firestationRepository.delete(anyString(), anyInt())).thenThrow(new NotFoundException("Firestation not found"));
        when(firestationRepository.find(anyString(), anyInt())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> firestationService.deleteFirestation(anyString(), anyInt()));

        assertEquals("Firestation not found", exception.getMessage());

        reset(firestationRepository);
    }

    @Test
    public void updateExistingFirestation() throws NotFoundException, InvalidObjectParameterException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.find("test address", 10)).thenReturn(Optional.of(firestation));

        firestationService.updateFirestation("test address", 10, firestation);

        verify(firestationRepository).update(anyString(), anyInt(), any(Firestation.class));
        reset(firestationRepository);
    }

    @Test
    public void updateNonExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.find("test address", 10)).thenReturn(null);
        when(firestationRepository.update(anyString(), anyInt(), any(Firestation.class))).thenReturn(null);

        Firestation toCheck = firestationService.updateFirestation("test address", 10, firestation);

        assertNull(toCheck);
        reset(firestationRepository);
    }

    @Test
    //@Disabled
    public void saveExistingFirestation() {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.find("test address", 15)).thenReturn(Optional.of(firestation));
        when(firestationRepository.save(any(Firestation.class))).thenReturn(null);

        Firestation toCheck = firestationService.saveFirestation(firestation);

        assertNull(toCheck);

        reset(firestationRepository);
    }

    @Test
    public void saveNonExistingFirestation() throws AlreadyExistsException, InvalidObjectParameterException {
        Firestation firestation = new Firestation();
        firestation.setAddress("test address");
        firestation.setStation(15);

        when(firestationRepository.find("test address", 15)).thenReturn(null);

        firestationService.saveFirestation(firestation);

        verify(firestationRepository).save(any(Firestation.class));

        reset(firestationRepository);
    }
}