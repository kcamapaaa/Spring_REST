package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.AbstractTestClass;
import com.vladislav.filestoragerest.model.Role;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.repository.UserRepository;
import com.vladislav.filestoragerest.service.UserService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.security.Principal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test-containers-flyway")
@Transactional
class UserServiceImplTest extends AbstractTestClass {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    private static final long NO_USER_ID = 10000;
    private static final long EXIST_USER_ID = 2;

    @Test
    void getAll_whenGetAllUsers_thenReturn4Users() {
        List<User> allUsers = userService.getAll();
        assertThat(allUsers.size()).isEqualTo(4);
        assertThat(allUsers.stream().map(User::getStatus).toList()).containsOnlyOnce(Status.DELETED);
    }

    @Test
    void getById_whenGetUserById_thenShouldReturnUser() {
        User userById = userService.getById(EXIST_USER_ID);
        assertThat(userById.getId()).isEqualTo(EXIST_USER_ID);
        assertThat(passwordEncoder.matches("test", userById.getPassword())).isTrue();
        assertThat(userById.getUsername()).isEqualTo("moderator");
        assertThat(userById.getRoles().stream().map(Role::getName)).contains("ROLE_USER","ROLE_MODERATOR");
    }

    @Test
    void getById_whenGetUserById_thenReturnNull() {
        User nullUser = userService.getById(NO_USER_ID);
        assertThat(nullUser).isNull();
    }

    @Test
    void getByUsername_whenGetByUsername_thenShouldReturnUser() {
        User admin = userService.getByUsername("admin");
        assertThat(admin.getUsername()).isEqualTo("admin");
    }

    @Test
    void getByUsername_whenGetUserWhichIsNotInDB_thenShouldReturnNull() {
        User userNull = userService.getByUsername(anyString());
        assertThat(userNull).isNull();
    }

    @Test
    void register_whenCorrected_shouldReturnNewUser() {
        User alexUser = new User();
        alexUser.setUsername("alex");
        alexUser.setPassword("test");

        User registered = userService.register(alexUser);

        assertThat(registered.getUsername()).isEqualTo(alexUser.getUsername());
        assertThat(passwordEncoder.matches("test", registered.getPassword())).isTrue();
        assertThat(registered.getRoles().stream().map(Role::getName)).containsOnly("ROLE_USER");
        assertThat(registered.getStatus()).isEqualTo(Status.ACTIVE);
        assertThat(userService.getAll().size()).isEqualTo(5);
    }

    @Test
    void delete_whenFoundUser_thenDeleteIt() {
        boolean deleted = userService.delete(EXIST_USER_ID);

        assertThat(deleted).isTrue();
        assertThat(userService.getById(EXIST_USER_ID).getStatus()).isEqualTo(Status.DELETED);
        assertThat(userService.getAll().size()).isEqualTo(4);
    }

    @Test
    void delete_whenUserNotFound_thenReturnNull() {
        boolean deleted = userService.delete(NO_USER_ID);
        assertThat(deleted).isFalse();
    }

    @Test
    void update_updateUser_WhenFound() {
        User alexUser = new User();
        alexUser.setId(4L);
        alexUser.setUsername("alex");
        alexUser.setPassword("test");

        User updated = userService.update(alexUser);

        assertThat(updated.getUsername()).isEqualTo(alexUser.getUsername());
        assertThat(updated.getId()).isEqualTo(alexUser.getId());
    }

    @Test
    void update_returnNull_whenUserNotFound() {
        User newUser = new User();
        newUser.setId(NO_USER_ID);

        User updated = userService.update(newUser);

        assertThat(updated).isNull();
    }

    @Test
    void getMyInfo() {
        User userInfo = userService.getMyInfo(() -> "user");
        assertThat(userInfo.getUsername()).isEqualTo("user");
    }

}