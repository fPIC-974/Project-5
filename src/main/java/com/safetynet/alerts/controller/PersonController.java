package com.safetynet.alerts.controller;

import com.safetynet.alerts.exception.AlreadyExistsException;
import com.safetynet.alerts.exception.NotFoundException;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.IPersonService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private static final Logger logger = LogManager.getLogger(PersonController.class);

    private final IPersonService personService;

    @Autowired
    public PersonController(IPersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/all")
    public List<Person> getPerson() {
        return personService.getPersons();
    }

    @GetMapping("/{lastName}/{firstName}")
    public Person getPersonByName(@PathVariable String lastName, @PathVariable String firstName) {
        return personService.getPersonByName(lastName,firstName);
    }

    @PostMapping
    public Person savePerson(@RequestBody Person person) throws AlreadyExistsException {
        return personService.savePerson(person);
    }

    @PutMapping("/{lastName}/{firstName}")
    public Person updatePerson(@PathVariable String lastName,
                               @PathVariable String firstName,
                               @RequestBody Person person) throws NotFoundException {
        return personService.updatePerson(lastName, firstName, person);
    }

    @DeleteMapping("/{lastName}/{firstName}")
    public void deletePerson(@PathVariable String lastName, @PathVariable String firstName) throws NotFoundException {
        personService.deletePersonByName(lastName, firstName);
    }
}
