package com.vladislav.filestoragerest.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerV1Test {

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("When unauthenticated user calls /api/v1/users then should return 403 error")
    void testUnauthenticatedUsersEndpoint() throws Exception {
        mvc.perform(get("/api/v1/users/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails
    void testAuthenticatedUsersEndpoint() throws Exception {
        mvc.perform(get("/api/v1/users/").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUser() {
    }

    @Test
    void uploadFile() {
    }

    @Test
    void downloadFile() {
    }

    @Test
    void deleteFile() {
    }
}