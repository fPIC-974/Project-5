package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Medicalrecord;

import java.util.Optional;

public interface IMedicalrecordRepository {
    Iterable<Medicalrecord> findAll();

    Optional<Medicalrecord> findByName(String lastName, String firstName);

    boolean existsByName(String lastName, String firstName);

    void delete(Medicalrecord medicalrecord);

    void deleteByName(String lastName, String firstName);

    Medicalrecord update(String lastName, String firstName, Medicalrecord medicalrecord);

    Medicalrecord save(Medicalrecord medicalrecord);
}
