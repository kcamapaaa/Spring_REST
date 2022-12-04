package com.vladislav.filestoragerest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladislav.filestoragerest.model.File;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileDto {
    private Long id;
    private String linkToFile;
    private String fileName;

    public File toFile() {
        File file = new File();
        file.setLocation(this.linkToFile);
        file.setId(this.id);
        file.setFileName(this.fileName);
        return file;
    }

    public static FileDto fromFile(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setId(file.getId());
        fileDto.setLinkToFile(file.getLocation());
        fileDto.setFileName(file.getFileName());
        return fileDto;
    }
}
