package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicalrecordServiceTest {

    @Mock
    private MedicalrecordRepository medicalrecordRepository;
    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private MedicalrecordService medicalrecordService;

    @Test
    public void getExistingMedicalrecord() {
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
    public void deleteExistingMedicalrecord() throws NotFoundException {

        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(true);

        medicalrecordService.deleteMedicalrecordByName("Doe", "John");

        verify(medicalrecordRepository).deleteByName(anyString(), anyString());
    }

    @Test
    public void deleteNonExistingMedicalrecord() {
        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> medicalrecordService.deleteMedicalrecordByName(anyString(), anyString()));

        assertEquals("Medicalrecord not found", exception.getMessage());
    }

    @Test
    public void saveExistingMedicalRecordWithExistingPerson() {
        Medicalrecord medicalrecord;
        medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");

        when(personRepository.existsByName(medicalrecord.getLastName(), medicalrecord.getFirstName())).thenReturn(true);
        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(true);

        AlreadyExistsException exception = assertThrows(AlreadyExistsException.class,
                () -> medicalrecordService.saveMedicalrecord(medicalrecord));

        assertEquals(
                "Medicalrecord already exists", exception.getMessage());
    }

    @Test
    public void saveExistingMedicalRecordWithoutExistingPerson() {
        Medicalrecord medicalrecord;
        medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");

        when(personRepository.existsByName(medicalrecord.getLastName(), medicalrecord.getFirstName())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> medicalrecordService.saveMedicalrecord(medicalrecord));

        assertEquals(
                "Person not found", exception.getMessage());
    }

    @Test
    public void saveNonExistingMedicalrecord() throws AlreadyExistsException, NotFoundException{
        Medicalrecord medicalrecord;
        medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");

        when(personRepository.existsByName(medicalrecord.getLastName(), medicalrecord.getFirstName())).thenReturn(true);
        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(false);

        medicalrecordService.saveMedicalrecord(medicalrecord);

        verify(medicalrecordRepository).save(any(Medicalrecord.class));
    }

    @Test
    public void updateExistingMedicalrecord() throws NotFoundException {
        Medicalrecord medicalrecord;
        medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");

        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(true);

        medicalrecordService.updateMedicalrecord("Doe", "John", medicalrecord);

        verify(medicalrecordRepository).update(anyString(), anyString(), any(Medicalrecord.class));
    }

    @Test
    public void updateNonExistingMedicalrecord() {
        Medicalrecord medicalrecord;
        medicalrecord = new Medicalrecord();
        medicalrecord.setLastName("Doe");
        medicalrecord.setFirstName("John");

        when(medicalrecordRepository.existsByName(anyString(), anyString())).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> medicalrecordService.updateMedicalrecord("Doe", "John", medicalrecord));

        assertEquals(
                "Medicalrecord not found", exception.getMessage());
    }
}