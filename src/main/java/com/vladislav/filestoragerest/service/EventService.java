package com.vladislav.filestoragerest.service;

import com.vladislav.filestoragerest.model.Event;

public interface EventService extends GenericService<Event, Long> {
    Event save(Event event);
    boolean delete (Long id);
}
