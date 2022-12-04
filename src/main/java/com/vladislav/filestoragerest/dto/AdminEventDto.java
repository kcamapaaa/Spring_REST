package com.vladislav.filestoragerest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladislav.filestoragerest.model.Event;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminEventDto {
    private Long id;
    private String fileName;
    private String username;
    private String action;
    private String status;

    public static AdminEventDto fromEvent(Event event) {
        AdminEventDto adminEventDto = new AdminEventDto();
        adminEventDto.setId(event.getId());
        adminEventDto.setFileName(event.getFile().getFileName());
        adminEventDto.setUsername(event.getUser().getUsername());
        adminEventDto.setAction(event.getAction().name());
        adminEventDto.setStatus(event.getStatus().name());
        return adminEventDto;
    }
}
