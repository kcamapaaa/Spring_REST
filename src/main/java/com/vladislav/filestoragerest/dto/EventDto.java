package com.vladislav.filestoragerest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladislav.filestoragerest.model.Event;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventDto {
    private Long id;
    private String fileName;
    private String username;
    private String action;

    public static EventDto fromEvent(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(eventDto.getId());
        eventDto.setFileName(event.getFile().getFileName());
        eventDto.setUsername(event.getUser().getUsername());
        eventDto.setAction(event.getAction().name());
        return eventDto;
    }
}
