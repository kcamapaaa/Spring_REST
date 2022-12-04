package com.vladislav.filestoragerest.repository;

import com.vladislav.filestoragerest.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
