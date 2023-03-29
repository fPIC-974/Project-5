package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Firestation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FirestationControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Test
    public void shouldGetFirestations() throws Exception {
        mockMvc.perform(get("/firestation/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", is("1509 Culver St")));
    }

    @Test
    public void shouldGetFirestationsByAddress() throws Exception {

        String path = UriUtils.encodePath("/firestation/addr/1509 Culver St", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        mockMvc.perform(get(decodedPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", is("1509 Culver St")))
                .andExpect(jsonPath("$[0].station", is(3)));
    }

    @Test
    public void shouldGetFirestationsByStation() throws Exception {

        mockMvc.perform(get("/firestation/stat/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].address", is("1509 Culver St")))
                .andExpect(jsonPath("$[0].station", is(3)));
    }


    @Test
    public void shouldGetFirestation() throws Exception {
        String path = UriUtils.encodePath("/firestation/1509 Culver St/3", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        mockMvc.perform(get(decodedPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("1509 Culver St")))
                .andExpect(jsonPath("$.station", is(3)));
    }

    @Test
    public void shouldSaveFirestation() throws Exception {
        Firestation firestation = new Firestation();
        firestation.setStation(10);
        firestation.setAddress("221B Baker Street");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("221B Baker Street")))
                .andExpect(jsonPath("$.station", is(10)));
    }

    @Test
    public void shouldUpdateFirestation() throws Exception {
        String path = UriUtils.encodePath("/firestation/489 Manchester St/4", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        Firestation firestation = new Firestation();
        firestation.setStation(4);
        firestation.setAddress("221B Baker Street");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(put(decodedPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firestation))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address", is("221B Baker Street")))
                .andExpect(jsonPath("$.station", is(4)));
    }

    // TODO - shouldDeleteFirestation() test
    @Test
    @Disabled
    public void shouldDeleteFirestation() throws Exception {
        String path = UriUtils.encodePath("/firestation/1509 Culver St/3", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        mockMvc.perform(delete(decodedPath))
                .andExpect(status().isOk());
    }
}