package com.safetynet.alerts.repository;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;

import java.util.List;
import java.util.Optional;

public interface IFirestationRepository {
    Iterable<Firestation> findAll();

    List<Firestation> findByAddress(String address);

    List<Firestation> findByStation(int station);

    Optional<Firestation> find(String address, int station);

    boolean exists(String address, int station);

    void delete(Firestation firestation) throws NotFoundException;

    void delete(String address, int station) throws NotFoundException;

    Firestation update(String address, int station, Firestation firestation) throws NotFoundException;

    Firestation save(Firestation firestation) throws AlreadyExistsException;
}
