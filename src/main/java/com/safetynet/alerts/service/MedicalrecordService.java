package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.repository.MedicalrecordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalrecordService {
    private static final Logger logger = LogManager.getLogger("Medicalrecord Service");

    private MedicalrecordRepository medicalrecordRepository;

    @Autowired
    public MedicalrecordService(MedicalrecordRepository medicalrecordRepository) {
        this.medicalrecordRepository = medicalrecordRepository;
    }

    public List<Medicalrecord> getMedicalrecords() {
        return (List<Medicalrecord>) medicalrecordRepository.findAll();
    }

    public Medicalrecord getMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.findByName(lastName, firstName).orElse(null);
    }

    public boolean existsMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.existsByName(lastName, firstName);
    }

    public void deleteMedicalrecordByName(String lastName, String firstName) {
        if (existsMedicalrecord(lastName, firstName)) {
            medicalrecordRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    public Medicalrecord saveMedicalrecord(Medicalrecord medicalrecord) {
        if (!existsMedicalrecord(medicalrecord.getLastName(), medicalrecord.getFirstName())) {
            return medicalrecordRepository.save(medicalrecord);
        } else {
            logger.error("Medicalrecord already exists : "
                    + medicalrecord.getFirstName() + ":"
                    + medicalrecord.getLastName());
            throw new IllegalStateException("Medicalrecord already exists");
        }
    }

    public Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) {
        if (existsMedicalrecord(lastName, firstName)) {
            return medicalrecordRepository.update(lastName, firstName, medicalrecord);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    public int getAge(String lastName, String firstName) {
        LocalDate dob = getMedicalrecord(lastName, firstName).getBirthdate();
        return LocalDate.now().compareTo(dob);
    }

    public boolean isMinor(String lastName, String firstName) {
        return getAge(lastName, firstName) <= 18;
    }
}
