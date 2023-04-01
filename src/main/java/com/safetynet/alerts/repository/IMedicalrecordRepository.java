package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Medicalrecord;

import java.util.Optional;

public interface IMedicalrecordRepository {
    Iterable<Medicalrecord> findAll();

    Optional<Medicalrecord> findByName(String lastName, String firstName);

    Optional<Medicalrecord> find(Medicalrecord medicalrecord);

    boolean exists(Medicalrecord medicalrecord);

    boolean existsByName(String lastName, String firstName);

    void delete(Medicalrecord medicalrecord) throws NotFoundException;

    void deleteByName(String lastName, String firstName) throws NotFoundException;

    Medicalrecord update(String lastName, String firstName, Medicalrecord medicalrecord) throws NotFoundException;

    Medicalrecord save(Medicalrecord medicalrecord) throws AlreadyExistsException;
}
