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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class AlertService implements IAlertService {
    private static final Logger logger = LogManager.getLogger(AlertService.class);

    private final IPersonService personService;

    private final IFirestationService firestationService;

    private final IMedicalrecordService medicalrecordService;

    @Autowired
    public AlertService(IPersonService personService,
                        IFirestationService firestationService,
                        IMedicalrecordService medicalrecordService) {
        this.personService = personService;
        this.firestationService = firestationService;
        this.medicalrecordService = medicalrecordService;
    }

    @Override
    public List<Person> getPersonsCoveredByStation(int station) {
        List<Firestation> firestations = firestationService.getFirestationByStation(station);
        return personService.getPersons().stream()
                .filter(firestation -> firestations.stream()
                        .anyMatch(person -> person.getAddress().equals(firestation.getAddress())))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> firestation(int station) {
        AtomicInteger majors = new AtomicInteger();
        AtomicInteger minors = new AtomicInteger();

        List<Person> persons = getPersonsCoveredByStation(station);

        List<ObjectNode> personsCovered = persons.stream()
                .map(m -> {
                    if (medicalrecordService.isMinor(m.getLastName(), m.getFirstName())) {
                        minors.getAndIncrement();
                    } else {
                        majors.getAndIncrement();
                    }

                    return personDetails(m, true, true, false, false, false);
                }).toList();

        Map<String, Object> mapOfPersonsCovered = new HashMap<>();
        mapOfPersonsCovered.put("minors", minors);
        mapOfPersonsCovered.put("majors", majors);
        mapOfPersonsCovered.put("persons", personsCovered);


        logger.info("Call   : firestation(" + station + ")");
        logger.info("Result : " + mapOfPersonsCovered);
        return mapOfPersonsCovered;
    }

    @Override
    public Map<String, Object> childAlert(String address) {
        // Children at address ?
        boolean hasChildren = personService.getPersonsByAddress(address).stream()
                .anyMatch(p -> medicalrecordService.isMinor(p.getLastName(), p.getFirstName()));

        List<JsonNode> listOfChildren = new ArrayList<>();
        List<JsonNode> listOfMembers = new ArrayList<>();
        Map<String, Object> medicalrecordsWithChildren = new HashMap<>();

        if (hasChildren) {
            for (Person person : personService.getPersonsByAddress(address)) {

                ObjectNode personInfo = personDetails(person, false, false, true, false, false);
                if (medicalrecordService.isMinor(person.getLastName(), person.getFirstName())) {
                    listOfChildren.add(personInfo);
                } else {
                    listOfMembers.add(personInfo);
                }
            }
        }

        medicalrecordsWithChildren.put("children", listOfChildren);
        medicalrecordsWithChildren.put("members", listOfMembers);

        logger.info("Call   : childAlert(" + address + ")");
        logger.info("Result : " + medicalrecordsWithChildren);
        return medicalrecordsWithChildren;
    }

    @Override
    public Set<String> phoneAlert(int station) {
        Set<String> phoneNumbers = getPersonsCoveredByStation(station).stream()
                .map(Person::getPhone)
                .collect(Collectors.toSet());

        logger.info("Call   : phoneAlert(" + station + ")");
        logger.info("Result : " + phoneNumbers);
        return phoneNumbers;
    }

    @Override
    public Map<String, Object> fire(String address) {
        Set<Integer> firestationsID = firestationService.getFirestationByAddress(address).stream()
                .filter(f -> f.getAddress().equals(address))
                .map(Firestation::getStation)
                .collect(Collectors.toSet());

        List<ObjectNode> persons = personService.getPersonsByAddress(address).stream()
                .map(p -> personDetails(p, false, true, true, false, true)).toList();

        Map<String, Object> mapOfPersons = new HashMap<>();
        if (firestationsID.isEmpty()) {
            mapOfPersons.put("address", "");
        }
        mapOfPersons.put("address", address);
        mapOfPersons.put("stationNumber", firestationsID);
        mapOfPersons.put("persons", persons);

        logger.info("Call   : fire(" + address + ")");
        logger.info("Result : " + mapOfPersons);
        return mapOfPersons;
    }

    @Override
    public JsonNode personInfo(String lastName, String firstName) {
        Person person = personService.getPersonByName(lastName, firstName);

        ObjectNode personInfo = personDetails(person, true, false, true, true, true);

        logger.info("Call   : personInfo(" + lastName + ", " + firstName + ")");
        logger.info("Result : " + personInfo);
        return personInfo;
    }

    @Override
    public Set<String> communityEmail(String city) {
        Set<String> emails = personService.getPersonsByCity(city).stream()
                .map(Person::getEmail)
                .collect(Collectors.toSet());

        logger.info("Call   : communityEmail(" + city + ")");
        logger.info("Result : " + emails);
        return emails;
    }

    @Override
    public Map<String, List<ObjectNode>> flood(int[] stations) {
        Set<Firestation> firestations = new HashSet<>();

        for (int station : stations) {
            firestations.addAll(firestationService.getFirestationByStation(station));
        }

        Set<Person> persons = new HashSet<>();

        for (Firestation station : firestations) {
            persons.addAll(getPersonsCoveredByStation(station.getStation()));
        }

        Map<String, List<Person>> test = persons.stream()
                .collect(Collectors.groupingBy(Person::getAddress));

        Map<String, List<ObjectNode>> mapOfNodes = new HashMap<>();
        for (var entry : test.entrySet()) {
            String key = entry.getKey();
            List<ObjectNode> personList = entry.getValue().stream()
                    .map(p -> personDetails(p, false, true, true, false, true)).toList();
            mapOfNodes.put(key, personList);
        }

        logger.info("Call   : flood(" + Arrays.toString(stations) + ")");
        logger.info("Result : " + mapOfNodes);
        return mapOfNodes;
    }

    @Override
    public ObjectNode personDetails(Person person,
                                    boolean address,
                                    boolean phone,
                                    boolean age,
                                    boolean email,
                                    boolean medicalinfo) {

        if (person == null) {
            return null;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode personInfo = objectMapper.createObjectNode();

        personInfo.put("firstName", person.getFirstName());
        personInfo.put("lastName", person.getLastName());
        if (address) { personInfo.put("address", person.getAddress()); }
        if (phone) { personInfo.put("phone", person.getPhone()); }
        if (age) { personInfo.put("age", medicalrecordService.getAge(person.getLastName(), person.getFirstName())); }
        if (email) { personInfo.put("email", person.getEmail()); }
        if (medicalinfo) {
            personInfo.set("medications", objectMapper.valueToTree(
                    medicalrecordService.getMedicalrecord(person.getLastName(), person.getFirstName()).getMedications()));
            personInfo.set("allergies", objectMapper.valueToTree(
                    medicalrecordService.getMedicalrecord(person.getLastName(), person.getFirstName()).getAllergies()));
        }

        return personInfo;
    }
}
