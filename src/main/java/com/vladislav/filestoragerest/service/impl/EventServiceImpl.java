package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.model.Event;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.repository.EventRepository;
import com.vladislav.filestoragerest.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAll() {
        List<Event> allEvents = eventRepository.findAll();
        log.info("IN getAll - {} events found", allEvents.size());
        return allEvents.isEmpty() ? null : allEvents;
    }

    @Override
    public Event getById(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if(event == null) {
            log.warn("IN getById - no event found by id: {}", id);
        }
        log.info("IN getById - event: {} found by id: {}", event, id);
        return event;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null) {
            event.setStatus(Status.DELETED);
            Event deletedEvent = eventRepository.save(event);
            log.info("IN deleted - event {} found by id: {}", deletedEvent, id);
            return true;
        } else {
            log.warn("IN delete - no event found by id: {}", id);
            return false;
        }
    }

    @Override
    @Transactional
    public Event save(Event event) {
        Event savedEvent = eventRepository.save(event);
        log.info("IN save - event with id: {} was saved", savedEvent.getId());
        return savedEvent;
    }
}
