package com.safetynet.alerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.safetynet.alerts.model.Medicalrecord;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class MedicalrecordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldGetMedicalrecords() throws Exception {
        mockMvc.perform(get("/medicalrecord/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("Fred")));
    }

    @Test
    public void shouldGetMedicalrecordByName() throws Exception {
        mockMvc.perform(get("/medicalrecord/Boyd/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Boyd")));
    }

    @Test
    public void shouldNotSaveMedicalrecordBecausePersonNotFound() throws Exception {
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
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldSaveMedicalrecordBecausePersonFound() throws Exception {
        // TODO

        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("Felicia");
        medicalrecord.setLastName("Boyd");
        medicalrecord.setBirthdate(LocalDate.of(1974, 1, 6));
        medicalrecord.setMedications(new ArrayList<>());
        List<String> allergies = new ArrayList<>();
        allergies.add("test");
        medicalrecord.setAllergies(allergies);

        shouldDeleteMedicalrecordFelicia();

        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        mockMvc.perform(post("/medicalrecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalrecord))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Boyd")))
                .andExpect(jsonPath("$.firstName", is("Felicia")))
                .andExpect(jsonPath("$.allergies.length()", is(1)));
    }

    @Test
    public void shouldUpdateMedicalrecord() throws Exception {
        Medicalrecord medicalrecord = new Medicalrecord();
        medicalrecord.setFirstName("Jacob");
        medicalrecord.setLastName("Boyd");
        medicalrecord.setBirthdate(LocalDate.of(1974, 1, 6));
        medicalrecord.setMedications(new ArrayList<>());
        medicalrecord.setAllergies(new ArrayList<>());

        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        mockMvc.perform((put("/medicalrecord/Boyd/Jacob"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalrecord))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName", is("Boyd")))
                .andExpect(jsonPath("$.firstName", is("Jacob")))
                .andExpect(jsonPath("$.birthdate", is("01/06/1974")));
    }

    @Test
    public void shouldDeleteMedicalrecord() {
        assertThatCode(() -> mockMvc.perform(delete("/medicalrecord/Boyd/John"))
                .andExpect(status().isOk()))
                .doesNotThrowAnyException();
    }

    public void shouldDeleteMedicalrecordFelicia() {
        assertThatCode(() -> mockMvc.perform(delete("/medicalrecord/Boyd/Felicia"))
                .andExpect(status().isOk()))
                .doesNotThrowAnyException();
    }
}