package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.repository.IMedicalrecordRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MedicalrecordService implements IMedicalrecordService {
    private static final Logger logger = LogManager.getLogger("Medicalrecord Service");

    private final IMedicalrecordRepository medicalrecordRepository;

    @Autowired
    public MedicalrecordService(IMedicalrecordRepository medicalrecordRepository) {
        this.medicalrecordRepository = medicalrecordRepository;
    }

    @Override
    public List<Medicalrecord> getMedicalrecords() {
        return (List<Medicalrecord>) medicalrecordRepository.findAll();
    }

    @Override
    public Medicalrecord getMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.findByName(lastName, firstName).orElse(null);
    }

    @Override
    public boolean existsMedicalrecord(String lastName, String firstName) {
        return medicalrecordRepository.existsByName(lastName, firstName);
    }

    @Override
    public void deleteMedicalrecordByName(String lastName, String firstName) {
        if (existsMedicalrecord(lastName, firstName)) {
            medicalrecordRepository.deleteByName(lastName, firstName);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    @Override
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

    @Override
    public Medicalrecord updateMedicalrecord(String lastName, String firstName, Medicalrecord medicalrecord) {
        if (existsMedicalrecord(lastName, firstName)) {
            return medicalrecordRepository.update(lastName, firstName, medicalrecord);
        } else {
            logger.error("Medicalrecord not found : " + firstName + ":" + lastName);
            throw new IllegalStateException("Medicalrecord not found");
        }
    }

    @Override
    public int getAge(String lastName, String firstName) {
        LocalDate dob = getMedicalrecord(lastName, firstName).getBirthdate();
        return LocalDate.now().compareTo(dob);
    }

    @Override
    public boolean isMinor(String lastName, String firstName) {
        return getAge(lastName, firstName) <= 18;
    }
}
