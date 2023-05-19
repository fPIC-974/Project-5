package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalrecordRepository;
import com.safetynet.alerts.repository.PersonRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalrecordServiceTest {

    @Mock
    private MedicalrecordRepository medicalrecordRepository;

    @InjectMocks
    private MedicalrecordService medicalrecordService;

    @Test
    public void shouldGetExistingMedicalrecord() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");
        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(medicalrecord));

        Medicalrecord toCheck = medicalrecordService.getMedicalrecord("Doe", "John");

        assertNotNull(toCheck);
        assertEquals("Doe", toCheck.getLastName());
        assertEquals("John", toCheck.getFirstName());
    }
    @Test
    public void shouldGetNonExistingMedicalrecord() {
        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.ofNullable(null));

        Medicalrecord toCheck = medicalrecordService.getMedicalrecord("Doe", "John");

        assertNull(toCheck);
    }

    @Test
    public void shouldDeleteExistingMedicalrecord() throws NotFoundException {
        assertDoesNotThrow(() -> medicalrecordService.deleteMedicalrecordByName(anyString(), anyString()));

        verify(medicalrecordRepository, times(1)).delete(anyString(), anyString());
    }

    @Test
    public void shouldDeleteNonExistingMedicalrecord() {
        when(medicalrecordRepository.delete(anyString(), anyString())).thenThrow(new NotFoundException("Medicalrecord not found"));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> medicalrecordService.deleteMedicalrecordByName(anyString(), anyString()));

        assertEquals("Medicalrecord not found", exception.getMessage());
    }

    /*@Test
    public void shouldSaveExistingMedicalRecord() {
        when(medicalrecordRepository.save(any(Medicalrecord.class))).thenThrow(new AlreadyExistsException("Medicalrecord already exists"));

        AlreadyExistsException aee = assertThrows(AlreadyExistsException.class,
                () -> medicalrecordService.saveMedicalrecord(new Medicalrecord()));

        assertTrue(aee.getMessage().contains("Medicalrecord already exists"));
    }*/

    /*@Test
    public void shouldSaveNonExistingMedicalrecord() throws AlreadyExistsException, NotFoundException{
        assertDoesNotThrow(() -> medicalrecordService.saveMedicalrecord(new Medicalrecord()));

        verify(medicalrecordRepository).save(any(Medicalrecord.class));
    }*/

    @Test
    public void shouldUpdateExistingMedicalrecord() throws NotFoundException {
        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(new Medicalrecord()));

        assertDoesNotThrow(() -> medicalrecordService.updateMedicalrecord("Doe", "John", new Medicalrecord()));

        verify(medicalrecordRepository).update(anyString(), anyString(), any(Medicalrecord.class));
        reset(medicalrecordRepository);
    }

    @Test
    public void shouldUpdateNonExistingMedicalrecord() {
        when(medicalrecordRepository.update(anyString(), anyString(), any(Medicalrecord.class))).thenThrow(new NotFoundException("Medicalrecord not found"));

        NotFoundException nfe = assertThrows(NotFoundException.class,
                () -> medicalrecordService.updateMedicalrecord("Doe", "John", new Medicalrecord()));

        assertTrue(nfe.getMessage().contains("Medicalrecord not found"));
    }

    @Test
    public void shouldGetAge() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setBirthdate(LocalDate.now().minusYears(20));

        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(medicalrecord));

        int toCheck = medicalrecordService.getAge("Doe", "John");

        assertEquals(20, toCheck);
    }

    @Test
    public void shouldReturnFalseForMajor() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setBirthdate(LocalDate.now().minusYears(20));

        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(medicalrecord));

        boolean toCheck = medicalrecordService.isMinor("Doe", "John");

        assertFalse(toCheck);
    }

    @Test
    public void shouldReturnTrueForMinor() {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setBirthdate(LocalDate.now().minusYears(16));

        when(medicalrecordRepository.findByName(anyString(), anyString())).thenReturn(Optional.of(medicalrecord));

        boolean toCheck = medicalrecordService.isMinor("Doe", "John");

        assertTrue(toCheck);
    }
}