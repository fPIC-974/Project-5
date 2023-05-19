package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.model.Person;

public interface ICrossService {

    Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord);
    void deletePersonByName(String lastName, String firstName);
}
