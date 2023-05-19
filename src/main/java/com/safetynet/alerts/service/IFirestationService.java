package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Firestation;

import java.util.List;

public interface IFirestationService {
    List<Firestation> getFirestations();

    List<Firestation> getFirestationByAddress(String address);

    List<Firestation> getFirestationByStation(int station);

    Firestation getFirestation(String address, int station);

    void deleteFirestation(Firestation firestation);

    void deleteFirestation(String address, int station);

    Firestation updateFirestation(String address, int station, Firestation firestation);

    Firestation saveFirestation(Firestation firestation);
}
