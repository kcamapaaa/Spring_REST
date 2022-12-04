package com.vladislav.filestoragerest.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseModel{
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users;

    public Role(String name) {
        this.name = name;
    }
}
