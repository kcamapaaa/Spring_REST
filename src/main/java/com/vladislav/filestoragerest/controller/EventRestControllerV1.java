package com.vladislav.filestoragerest.controller;

import com.vladislav.filestoragerest.dto.AdminEventDto;
import com.vladislav.filestoragerest.model.Event;
import com.vladislav.filestoragerest.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/")
public class EventRestControllerV1 {
    private final EventService eventService;

    @Autowired
    public EventRestControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<AdminEventDto>> getAllEvents() {
        List<Event> allEvents = eventService.getAll();
        if(allEvents.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        List<AdminEventDto> adminEventsDto = allEvents.stream().map(AdminEventDto::fromEvent).toList();
        return new ResponseEntity<>(adminEventsDto, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<AdminEventDto> getEventById(@PathVariable(value = "id") Long id) {
        Event eventById = eventService.getById(id);
        if(eventById == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            AdminEventDto adminEventDto = AdminEventDto.fromEvent(eventById);
            return new ResponseEntity<>(adminEventDto, HttpStatus.OK);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Boolean> deleteEventById(@PathVariable(value = "id") Long id) {
        boolean deleted = eventService.delete(id);
        if(!deleted) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }
}
