package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureMockMvc
@Transactional
class UserRestControllerV1Test extends AbstractTestClass {

    private static final String BASE_URL = "/api/v1/users/";
    public static final String PRESENT_USER_ID = "1";
    public static final String NO_USER_ID = "10000";
    public static final String ALL_USERS_JSON = "[{\"id\":1,\"username\":\"user\",\"status\":\"ACTIVE\"}," +
            "{\"id\":2,\"username\":\"moderator\",\"status\":\"ACTIVE\"}," +
            "{\"id\":3,\"username\":\"admin\",\"status\":\"ACTIVE\"}," +
            "{\"id\":4,\"username\":\"vasya\",\"status\":\"DELETED\"}]";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    void getAllUsers_testUnauthenticatedUsersEndpoint() throws Exception {
        mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void getAllUsers_testWithUserRole_andExpectForbidden() throws Exception {
        mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getAllUsers_testWithModeratorRole_andExpectOK() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains(ALL_USERS_JSON);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_testWithAdminRole_andExpectOk() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains(ALL_USERS_JSON);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserByID_userRole_shouldReturnForbidden() throws Exception {
        mvc.perform(get(BASE_URL + PRESENT_USER_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void getUserByID_moderatorRole_shouldReturnOk() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + PRESENT_USER_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains("{\"id\":1,\"username\":\"user\",\"status\":\"ACTIVE\"}");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByID_adminRole_shouldReturnOk() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + PRESENT_USER_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains("{\"id\":1,\"username\":\"user\",\"status\":\"ACTIVE\"}");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserByID_adminRole_shouldReturnNoContent() throws Exception {
        mvc.perform(get(BASE_URL + NO_USER_ID).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyInfo_shouldSendWhenUserFound() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "myInfo").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();

        assertThat(result).contains("{\"id\":1,\"username\":\"user\"}");
    }

    @Test
    @WithMockUser
    void updateUser_shouldReturnForbidden_whenUserRole() throws Exception {
        returnMvcResultForUpdateTests(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void updateUser_shouldReturnForbidden_whenModeratorRole() throws Exception {
        returnMvcResultForUpdateTests(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUser_shouldReturnUpdateUser_whenAdminRole() throws Exception {
        MvcResult mvcResult = returnMvcResultForUpdateTests(status().isOk()).andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        assertThat(result).contains("{\"id\":4,\"username\":\"alex\",\"status\":\"DELETED\"}");

        //User updatedUser = userService.getById(4L);
        //assertThat(passwordEncoder.matches(updatedUser.getPassword(), "alex")).isTrue();
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void delete_shouldReturnForbidden_whenRoleIsModerator() throws Exception {
        mvc.perform(delete(BASE_URL + PRESENT_USER_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnOk_whenRoleIsAdmin() throws Exception {
        MvcResult mvcResult = returnMvcResultForDeleteTests("1");
        String result = convertMvcResultToString(mvcResult);
        User userById = userService.getById(1L);

        assertThat(userById.getStatus()).isEqualTo(Status.DELETED);
        assertThat(result).isEqualTo("true");

    }

    private ResultActions returnMvcResultForUpdateTests(ResultMatcher status) throws Exception{
        return mvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":4,\"username\":\"alex\",\"password\": \"alex\"}"))
                .andExpect(status);
    }

    private MvcResult returnMvcResultForDeleteTests(String str) throws Exception{
        return mvc.perform(delete(BASE_URL + str))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String convertMvcResultToString(MvcResult mvcResult) throws UnsupportedEncodingException {
        return mvcResult.getResponse().getContentAsString();
    }
}