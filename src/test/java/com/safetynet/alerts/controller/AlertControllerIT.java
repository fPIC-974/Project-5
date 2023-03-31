package com.safetynet.alerts.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext
class AlertControllerIT {

    @Autowired
    public MockMvc mockMvc;

    @Test
    public void shouldGetMapFromFirestation() throws Exception {
        mockMvc.perform(get("/firestation?stationNumber=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['minors']", is(1)))
                .andExpect(jsonPath("$['majors']", is(5)))
                .andExpect(jsonPath("$['persons'].length()", is(6)));
    }

    @Test
    public void shouldGetMapFromChildAlert() throws Exception {
        String path = UriUtils.encodePath("/childAlert?address=1509 Culver St", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        mockMvc.perform(get(decodedPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['children'].length()", is(2)))
                .andExpect(jsonPath("$['members'].length()", is(4)))
                .andExpect(jsonPath("$['members'][0].firstName", is("Fred")));
    }

    @Test
    public void shouldGetSetFromPhoneAlert() throws Exception {
        mockMvc.perform(get("/phoneAlert?firestation=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(4)));
    }

    @Test
    public void shouldGetMapFromFire() throws Exception {
        String path = UriUtils.encodePath("/fire?address=1509 Culver St", "UTF-8");
        String decodedPath = UriUtils.decode(path, "UTF-8");

        mockMvc.perform(get(decodedPath))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['address']", is("1509 Culver St")))
                .andExpect(jsonPath("$['stationNumber'][0]", is(3)))
                .andExpect(jsonPath("$['persons'].length()", is(6)));
    }

    @Test
    public void shouldGetMapFromFlood() throws Exception {
        mockMvc.perform(get("/flood?stations=1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['947 E. Rose Dr'].length()", is(3)));
    }

    @Test
    public void shouldGetPersonInfo() throws Exception {
        mockMvc.perform(get("/personInfo?lastName=Boyd&firstName=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['firstName']", is("John")))
                .andExpect(jsonPath("$['lastName']", is("Boyd")))
                .andExpect(jsonPath("$['medications'].length()", is(2)))
                .andExpect(jsonPath("$['allergies'].length()", is(1)));
    }

    @Test
    public void shouldGetSetFromCommunityEmail() throws Exception {
        mockMvc.perform(get("/communityEmail?city=Culver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(15)));
    }
}