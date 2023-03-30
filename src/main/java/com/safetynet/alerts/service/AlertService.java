package com.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.Person;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AlertService {
    private static final Logger logger = LogManager.getLogger("Alert Service");

    @Autowired
    private PersonService personService;

    @Autowired
    private FirestationService firestationService;

    @Autowired
    private MedicalrecordService medicalrecordService;

    public List<Person> getPersonsCoveredByStation(int station) {
        List<Firestation> firestations = firestationService.getFirestationByStation(station);
        return personService.getPersons().stream()
                .filter(firestation -> firestations.stream()
                        .anyMatch(person -> person.getAddress().equals(firestation.getAddress())))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getPersonsCovered(int station) {
        AtomicInteger majors = new AtomicInteger();
        AtomicInteger minors = new AtomicInteger();

        ObjectMapper objectMapper = new ObjectMapper();

        List<Person> persons = getPersonsCoveredByStation(station);

        List<ObjectNode> personsCovered = persons.stream()
                .map(m -> {
                    ObjectNode personInfo = objectMapper.createObjectNode();
                    personInfo.put("firstName", m.getFirstName());
                    personInfo.put("lastName", m.getLastName());
                    personInfo.put("address", m.getAddress());
                    personInfo.put("phone", m.getPhone());

                    if (medicalrecordService.isMinor(m.getLastName(), m.getFirstName())) {
                        minors.getAndIncrement();
                    } else {
                        majors.getAndIncrement();
                    }

                    return personInfo;
                }).toList();

        Map<String, Object> mapOfPersonsCovered = new HashMap<>();
        mapOfPersonsCovered.put("minors", minors);
        mapOfPersonsCovered.put("majors", majors);
        mapOfPersonsCovered.put("persons", personsCovered);

        return mapOfPersonsCovered;
    }

    /*public List<Person> getPersonsByAddress(String address) {
        // TODO Migrate to PersonService
        return personService.getPersons().stream()
                .filter(person -> person.getAddress().equals(address))
                .collect(Collectors.toList());
    }*/

    public Map<String, Object> childAlert(String address) {
        // Children at address ?
        boolean hasChildren = personService.getPersonsByAddress(address).stream()
                .anyMatch(p -> medicalrecordService.isMinor(p.getLastName(), p.getFirstName()));

        List<JsonNode> listOfChildren = new ArrayList<>();
        List<JsonNode> listOfMembers = new ArrayList<>();
        Map<String, Object> medicalrecordsWithChildren = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();

        if (hasChildren) {
            for (Person person : personService.getPersonsByAddress(address)) {
                ObjectNode personInfo = objectMapper.createObjectNode();
                personInfo.put("firstName", person.getFirstName());
                personInfo.put("lastName", person.getLastName());
                personInfo.put("age", medicalrecordService.getAge(person.getLastName(), person.getFirstName()));

                if (medicalrecordService.isMinor(person.getLastName(), person.getFirstName())) {
                    listOfChildren.add(personInfo);
                } else {
                    listOfMembers.add(personInfo);
                }
            }
        }

        medicalrecordsWithChildren.put("children", listOfChildren);
        medicalrecordsWithChildren.put("members", listOfMembers);

        return medicalrecordsWithChildren;
    }

    public Set<String> phoneAlert(int station) {
        return getPersonsCoveredByStation(station).stream()
                .map(Person::getPhone)
                .collect(Collectors.toSet());
    }

    public Map<String, Object> fire(String address) {
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Integer> firestationsID = firestationService.getFirestationByAddress(address).stream()
                .filter(f -> f.getAddress().equals(address))
                .map(Firestation::getStation)
                .collect(Collectors.toSet());

        List<ObjectNode> persons = personService.getPersonsByAddress(address).stream()
                .map(p -> {
                    ObjectNode personInfo = objectMapper.createObjectNode();

                    personInfo.put("firstName", p.getFirstName());
                    personInfo.put("lastName", p.getLastName());
                    personInfo.put("phone", p.getPhone());
                    personInfo.put("age", medicalrecordService.getAge(p.getLastName(), p.getFirstName()));
                    personInfo.set("medications", objectMapper.valueToTree(
                            medicalrecordService.getMedicalrecord(p.getLastName(), p.getFirstName()).getMedications()));
                    personInfo.set("allergies", objectMapper.valueToTree(
                            medicalrecordService.getMedicalrecord(p.getLastName(), p.getFirstName()).getAllergies()));

                    return personInfo;
                }).toList();


        Map<String, Object> mapOfPersons = new HashMap<>();
        mapOfPersons.put("address", address);
        mapOfPersons.put("stationNumber", firestationsID);
        mapOfPersons.put("persons", persons);

        return mapOfPersons;
    }

    public JsonNode personInfo(String lastName, String firstName) {

        ObjectMapper objectMapper = new ObjectMapper();

        Person person = personService.getPersonByName(lastName, firstName);

        ObjectNode personDetails = objectMapper.createObjectNode();
        personDetails.put("firstName", person.getFirstName());
        personDetails.put("lastName", person.getLastName());
        personDetails.put("address", person.getAddress());
        personDetails.put("age", medicalrecordService.getAge(lastName, firstName));
        personDetails.put("email", person.getEmail());
        personDetails.set("medications", objectMapper.valueToTree(
                medicalrecordService.getMedicalrecord(person.getLastName(), person.getFirstName()).getMedications()));
        personDetails.set("allergies", objectMapper.valueToTree(
                medicalrecordService.getMedicalrecord(person.getLastName(), person.getFirstName()).getAllergies()));

        return personDetails;
    }

    public Set<String> communityEmail(String city) {
        return personService.getPersonsByCity(city).stream()
                .map(Person::getEmail)
                .collect(Collectors.toSet());
    }

    public Map<String, List<ObjectNode>> flood(int[] stations) {
        Set<Firestation> firestations = new HashSet<>();
        Map<String, Object> mapOfPersons = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        for (int station : stations) {
            firestations.addAll(firestationService.getFirestationByStation(station));
        }

        Set<Person> persons = new HashSet<>();

        for (Firestation station : firestations) {
            List<Person> personList = getPersonsCoveredByStation(station.getStation());


            persons.addAll(getPersonsCoveredByStation(station.getStation()));
        }

        Map<String, List<Person>> test = persons.stream()
                .collect(Collectors.groupingBy(Person::getAddress));

        Map<String, List<ObjectNode>> mapOfNodes = new HashMap<>();
        for (var entry : test.entrySet()) {
            String key = entry.getKey();
            List<ObjectNode> personList = entry.getValue().stream()
                    .map(p -> {
                        ObjectNode personInfo = objectMapper.createObjectNode();

                        personInfo.put("firstName", p.getFirstName());
                        personInfo.put("lastName", p.getLastName());
                        personInfo.put("phone", p.getPhone());
                        personInfo.put("age", medicalrecordService.getAge(p.getLastName(), p.getFirstName()));
                        personInfo.set("medications", objectMapper.valueToTree(
                                medicalrecordService.getMedicalrecord(p.getLastName(), p.getFirstName()).getMedications()));
                        personInfo.set("allergies", objectMapper.valueToTree(
                                medicalrecordService.getMedicalrecord(p.getLastName(), p.getFirstName()).getAllergies()));

                        return personInfo;
                    }).toList();
            mapOfNodes.put(key, personList);
        }

        return mapOfNodes;
    }
}
