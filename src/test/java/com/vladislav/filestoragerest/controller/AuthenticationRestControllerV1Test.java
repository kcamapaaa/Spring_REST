package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.AbstractTestClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureMockMvc
@Transactional
public class AuthenticationRestControllerV1Test extends AbstractTestClass {
    @Autowired
    MockMvc mvc;
    private static final String BASE_URL = "/api/v1/auth/";
    private static final String RANDOM_USER = "{\"username\":\"gosha\",\"password\":\"test\"}";
    private static final String PRESENT_USER = "{\"username\":\"user\",\"password\":\"test\"}";

    @Test
    public void login_shouldThrowException_ifNoUserIsPresent() throws Exception {
        mvc.perform(post(BASE_URL + "login").contentType(MediaType.APPLICATION_JSON).content(RANDOM_USER))
                .andExpect(status().isForbidden());
    }

    @Test
    public void login_shouldReturn200_ifUserIsPresent() throws Exception {
        mvc.perform(post(BASE_URL + "login").contentType(MediaType.APPLICATION_JSON).content(PRESENT_USER))
                .andExpect(status().isOk());
    }
}
