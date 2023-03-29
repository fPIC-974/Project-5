package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.safetynet.alerts.model.Medicalrecord;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MedicalrecordControllerIT {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void shouldGetMedicalrecords() throws Exception {
        mockMvc.perform(get("/medicalrecord/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void shouldGetMedicalrecordByName() throws Exception {
        mockMvc.perform(get("/medicalrecord/Boyd/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Boyd")));
    }
    @Test
    public void shouldSaveMedicalrecord() throws Exception {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("John");
        medicalrecord.setLastName("Doe");
        medicalrecord.setBirthdate(LocalDate.of(1974, 1, 6));
        medicalrecord.setMedications(new ArrayList<>());
        medicalrecord.setAllergies(new ArrayList<>());

        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalrecord))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    public void shouldUpdateMedicalrecord() throws Exception {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("John");
        medicalrecord.setLastName("Boyd");
        medicalrecord.setBirthdate(LocalDate.of(1974, 1, 6));
        medicalrecord.setMedications(new ArrayList<>());
        medicalrecord.setAllergies(new ArrayList<>());

        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        mockMvc.perform((put("/medicalrecord/Boyd/John"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalrecord))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName", is("Boyd")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.birthdate", is("01/06/1974")));
    }

    // TODO - shouldDeletePerson() test
    @Test
    @Disabled
    public void shouldDeleteMedicalrecord() throws Exception {
        mockMvc.perform(get("/medicalrecord/Boyd/John"))
                .andExpect(status().isOk());
    }
}