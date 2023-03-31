package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Medicalrecord;

import java.util.List;

public interface IMedicalrecordService {
    List<Medicalrecord> getMedicalrecords();

    Medicalrecord getMedicalrecord(String lastName, String firstName);

    boolean existsMedicalrecord(String lastName, String firstName);

    void deleteMedicalrecordByName(String lastName, String firstName);

    Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord);

    Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord);

    int getAge(String lastName, String firstName);

    boolean isMinor(String lastName, String firstName);
}
