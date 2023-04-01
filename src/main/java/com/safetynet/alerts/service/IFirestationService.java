package com.safetynet.alerts.service;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Firestation;

import java.util.List;

public interface IFirestationService {
    List<Firestation> getFirestations();

    List<Firestation> getFirestationByAddress(String address);

    List<Firestation> getFirestationByStation(int station);

    Firestation getFirestation(String address, int station);

    boolean existsFirestation(String address, int station);

    void deleteFirestation(Firestation firestation) throws NotFoundException;

    void deleteFirestation(String address, int station) throws NotFoundException;

    Firestation updateFirestation(String address, int station, Firestation firestation) throws NotFoundException;

    Firestation saveFirestation(Firestation firestation) throws AlreadyExistsException;
}
