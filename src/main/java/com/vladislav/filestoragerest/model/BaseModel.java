package com.vladislav.filestoragerest.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(10) default 'ACTIVE'", nullable = false)
    private Status status = Status.ACTIVE;

    @Column(name = "creation_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "updated")
    @LastModifiedDate
    private LocalDateTime lastUpdated = LocalDateTime.now();
}
