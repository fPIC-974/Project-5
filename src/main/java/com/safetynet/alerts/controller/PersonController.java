package com.safetynet.alerts.controller;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.ICrossService;
import com.safetynet.alerts.service.IPersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private static final Logger logger = LogManager.getLogger(PersonController.class);

    private final IPersonService personService;
    private final ICrossService crossService;

    @Autowired
    public PersonController(ICrossService crossService, IPersonService personService) {
        this.personService = personService;
        this.crossService = crossService;
    }

    @GetMapping("/all")
    public List<Person> getPerson() {
        logger.info("GET REQUEST : /person/all");
        List<Person> result = personService.getPersons();

        logger.info("RESPONSE : " + result);
        return result;
    }

    @GetMapping("/{lastName}/{firstName}")
    public ResponseEntity<?> getPersonByName(@PathVariable String lastName, @PathVariable String firstName) {
        logger.info("GET REQUEST : /person/" + lastName + "/" + firstName);
        Person result = personService.getPersonByName(lastName,firstName);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        if (result == null) {
            JsonObject emptyJson = new JsonObject();

            logger.info("RESPONSE : " + emptyJson);
            return new ResponseEntity<>(emptyJson, headers, HttpStatus.OK);
        }

        logger.info("RESPONSE : " + result);
        return new ResponseEntity<>(result,headers, HttpStatus.OK);
    }

    @PostMapping
    public Person savePerson(@RequestBody Person person) {
        logger.info("POST REQUEST : /person + Body = " + person);

        Person result = personService.savePerson(person);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @PutMapping("/{lastName}/{firstName}")
    public Person updatePerson(@PathVariable String lastName,
                               @PathVariable String firstName,
                               @RequestBody Person person) {
        logger.info("PUT REQUEST : /" + lastName + "/" + firstName + " + Body = " + person);

        Person result = personService.updatePerson(lastName, firstName, person);

        logger.info("RESPONSE : " + result);
        return result;
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public void deletePerson(@PathVariable String lastName, @PathVariable String firstName) {
        logger.info("DELETE REQUEST : /" + lastName + "/" + firstName);

        crossService.deletePersonByName(lastName, firstName);
    }
}
