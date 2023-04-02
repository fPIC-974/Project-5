package com.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.Person;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IAlertService {
    List<Person> getPersonsCoveredByStation(int station);

    Map<String, Object> firestation(int station);

    Map<String, Object> childAlert(String address);

    Set<String> phoneAlert(int station);

    Map<String, Object> fire(String address);

    JsonNode personInfo(String lastName, String firstName);

    Set<String> communityEmail(String city);

    Map<String, List<ObjectNode>> flood(int[] stations);

    ObjectNode personDetails(Person person,
                             boolean address,
                             boolean phone,
                             boolean age,
                             boolean email,
                             boolean medicalinfo);
}
