package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;

import java.util.List;

public interface IMedicalrecordService {
    List<Medicalrecord> getMedicalrecords();

    Medicalrecord getMedicalrecord(String lastName, String firstName);

    boolean existsMedicalrecord(String lastName, String firstName);

    void deleteMedicalrecordByName(String lastName, String firstName) throws NotFoundException;

    Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord) throws AlreadyExistsException;

    Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) throws NotFoundException;

    int getAge(String lastName, String firstName);

    boolean isMinor(String lastName, String firstName);
}
