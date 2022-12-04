package com.vladislav.filestoragerest.repository;

import com.vladislav.filestoragerest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String name);
}
