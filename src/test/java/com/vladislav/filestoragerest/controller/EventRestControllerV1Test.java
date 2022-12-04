package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.model.Event;
import com.vladislav.filestoragerest.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@AutoConfigureMockMvc
@Transactional
@Sql({"classpath:add-files.sql", "classpath:add-events.sql"})
class EventRestControllerV1Test extends AbstractTestClass {

    private static final String BASE_URL = "/api/v1/events/";
    @Autowired
    MockMvc mvc;
    @Autowired
    EventService eventService;


    @Test
    @WithMockUser(roles = "USER")
    public void getAllEvents_shouldReturn403_whenUser() throws Exception{
        mvc.perform(get(BASE_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void getAllEvents_shouldReturn200_whenModerator() throws Exception{
        String result = mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

//        assertThat(result).isEqualTo("[{\"id\":1,\"filename\":\"hello.txt\",\"username\":\"user\",\"action\":\"UPLOADED\",\"status\":\"ACTIVE\"}," +
//                "{\"id\":2,\"fileName\":\"hello.txt\",\"username\":\"moderator\",\"action\":\"UPLOADED\",\"status\":\"ACTIVE\"}," +
//                "{\"id\":3,\"fileName\":\"hello.txt\",\"username\":\"user\",\"action\":\"DELETED\",\"status\":\"ACTIVE\"}]");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllEvents_shouldReturn200_whenAdmin() throws Exception{
        String result = mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

//        assertThat(result).isEqualTo("[{\"id\":1,\"filename\":\"hello.txt\",\"username\":\"user\",\"action\":\"UPLOADED\",\"status\":\"ACTIVE\"}," +
//                "{\"id\":2,\"fileName\":\"hello.txt\",\"username\":\"moderator\",\"action\":\"UPLOADED\",\"status\":\"ACTIVE\"}," +
//                "{\"id\":3,\"fileName\":\"hello.txt\",\"username\":\"user\",\"action\":\"DELETED\",\"status\":\"ACTIVE\"}]");
    }

    @Test
    @WithMockUser
    public void deleteEventById_shouldReturn430_whenUser() throws Exception {
        mvc.perform(delete(BASE_URL + "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    public void deleteEventById_shouldReturn200_whenModerator() throws Exception {
        String contentAsString = getStringContext();

        Event eventById = eventService.getById(1L);

        assertThat(contentAsString).isEqualTo("true");
        assertThat(eventById.getStatus().name()).isEqualTo("DELETED");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void deleteEventById_shouldReturn200_whenAdmin() throws Exception {
        String contentAsString = getStringContext();

        Event eventById = eventService.getById(1L);

        assertThat(contentAsString).isEqualTo("true");
        assertThat(eventById.getStatus().name()).isEqualTo("DELETED");
    }

    private String getStringContext() throws Exception {
        return  mvc.perform(delete(BASE_URL + "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

}