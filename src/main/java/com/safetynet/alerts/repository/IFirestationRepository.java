package com.safetynet.alerts.repository;

import com.safetynet.alerts.model.Firestation;

import java.util.List;
import java.util.Optional;

public interface IFirestationRepository {
    Iterable<Firestation> findAll();

    List<Firestation> findByAddress(String address);

    List<Firestation> findByStation(int station);

    Optional<Firestation> find(String address, int station);

    boolean exists(String address, int station);

    void delete(Firestation firestation);

    void delete(String address, int station);

    Firestation update(String address, int station, Firestation firestation);

    Firestation save(Firestation firestation);
}
