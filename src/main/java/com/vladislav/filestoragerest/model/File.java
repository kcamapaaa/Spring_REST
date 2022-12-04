package com.vladislav.filestoragerest.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "Files")
public class File extends BaseModel{
    @Column(name = "location", nullable = false)
    private String location;
    @Column(name = "file_name", nullable = false, unique = true)
    private String fileName;
    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> event;

    public File() {
    }

    public File(String fileName) {
        this.fileName = fileName;
    }
    public File(String location, String fileName) {
        this.location = location;
        this.fileName = fileName;
    }
}
