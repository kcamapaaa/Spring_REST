package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.model.Action;
import com.vladislav.filestoragerest.model.Event;
import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.service.EventService;
import com.vladislav.filestoragerest.service.FileService;
import com.vladislav.filestoragerest.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@Transactional
public class EventServiceImplTest extends AbstractTestClass {
    private static final String ADD_FILES_TO_DB = "classpath:add-files.sql";
    private static final String ADD_EVENTS_TO_DB = "classpath:add-events.sql";

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;

    @Test
    public void getAll_ShouldReturnNull_whenNoEvents() {
        List<Event> allEvents = eventService.getAll();

        assertThat(allEvents).isNull();
    }

    @Test
    @Sql({ADD_FILES_TO_DB, ADD_EVENTS_TO_DB})
    public void getAll_ShouldReturnEvents_whenPresent() {
        List<Event> allEvents = eventService.getAll();

        assertThat(allEvents.size()).isEqualTo(3);
    }

    @Test
    public void getById_shouldReturnNull_whenNoEvents() {
        Event eventByID = eventService.getById(1L);

        assertThat(eventByID).isNull();
    }

    @Test
    @Sql({ADD_FILES_TO_DB, ADD_EVENTS_TO_DB})
    public void getById_shouldReturnEvent_whenPresent() {
        Event eventByID = eventService.getById(1L);

        assertThat(eventByID).isNotNull();
        assertThat(eventByID.getId()).isEqualTo(1L);
        assertThat(eventByID.getFile().getFileName()).isEqualTo("hello.txt");
    }

    @Test
    public void delete_whenNoEvent_thenReturnFalse() {
        boolean deleted = eventService.delete(10000L);

        assertThat(deleted).isFalse();
    }

    @Test
    @Sql({ADD_FILES_TO_DB, ADD_EVENTS_TO_DB})
    public void delete_whenEventIsPresent_thenShouldDeleteIt() {
        boolean deleted = eventService.delete(1L);

        assertThat(deleted).isTrue();
        assertThat(eventService.getById(1L).getStatus().name()).isEqualTo("DELETED");
    }

    @Test
    @Sql({ADD_FILES_TO_DB})
    public void save_shouldReturnEvent_whenCorrectlySaved() {
        User user = userService.getById(1L);
        File fileById = fileService.getById(1L);
        Event event = new Event(Action.UPLOADED, user, fileById);
        Event savedEvent = eventService.save(event);

        assertThat(savedEvent.getFile().getFileName()).isEqualTo("hello.txt");
        assertThat(savedEvent.getUser().getUsername()).isEqualTo("user");
        assertThat(savedEvent.getUser().getId()).isEqualTo(1L);
        assertThat(savedEvent.getFile().getId()).isEqualTo(1L);
        assertThat(savedEvent.getStatus().name()).isEqualTo("ACTIVE");
        assertThat(eventService.getAll().size()).isEqualTo(1);
    }
}
