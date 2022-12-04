package com.vladislav.filestoragerest.repository;

import com.vladislav.filestoragerest.model.File;
import com.vladislav.filestoragerest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByFileName(String name);
}
