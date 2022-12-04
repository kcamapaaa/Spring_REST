package com.vladislav.filestoragerest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.Status;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminFileDto {
    private Long id;
    private String linkToFile;
    private String fileName;
    private String status;

    public File toFile() {
        File file = new File();
        file.setLocation(this.linkToFile);
        file.setId(this.id);
        file.setFileName(this.fileName);
        file.setStatus(Status.valueOf(status));
        return file;
    }

    public static AdminFileDto fromFile(File file) {
        AdminFileDto adminFileDto = new AdminFileDto();
        adminFileDto.setId(file.getId());
        adminFileDto.setLinkToFile(file.getLocation());
        adminFileDto.setFileName(file.getFileName());
        adminFileDto.setStatus(file.getStatus().name());
        return adminFileDto;
    }
}
