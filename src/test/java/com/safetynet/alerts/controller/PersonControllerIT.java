package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PersonControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetPersons() throws Exception {
        mockMvc.perform(get("/person/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("Fred")));
    }

    @Test
    public void shouldGetPersonByName() throws Exception {
        mockMvc.perform(get("/person/Boyd/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Boyd")));
    }

    @Test
    public void shouldSavePerson() throws Exception {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setAddress("1509 Culver St");
        person.setCity("Culver");
        person.setZip(97451);
        person.setPhone("841-874-6512");
        person.setEmail("jdoe@email.com");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void shouldUpdatePerson() throws Exception {
        Person person = new Person();
        person.setFirstName("Fred");
        person.setLastName("Picard");
        person.setAddress("1509 Culver St");
        person.setCity("Culver");
        person.setZip(97451);
        person.setPhone("841-874-6512");
        person.setEmail("fred@email.com");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform((put("/person/Picard/Fred"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(person))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName", is("Picard")))
                .andExpect(jsonPath("$.firstName", is("Fred")))
                .andExpect(jsonPath("$.email", is("fred@email.com")));
    }

    // TODO - shouldDeletePerson() test
    @Test
    @Disabled
    public void shouldDeletePerson() throws Exception {
        mockMvc.perform(get("/person/Boyd/John"))
                .andExpect(status().isOk());
    }
}