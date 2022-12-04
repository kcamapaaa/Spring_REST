package com.vladislav.filestoragerest.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "Events")
public class Event extends BaseModel {
    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private Action action;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    public Event() {

    }

    public Event(Action action, User user, File file) {
        this.action = action;
        this.user = user;
        this.file = file;
    }
}
