package com.vladislav.filestoragerest.service.impl;

import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.model.Role;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.repository.UserRepository;
import com.vladislav.filestoragerest.repository.RoleRepository;
import com.vladislav.filestoragerest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(@Lazy UserRepository userRepository,@Lazy RoleRepository roleRepository,@Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAll() {
        List<User> allUsers = userRepository.findAll();
        log.info("IN getAll - {} users found", allUsers.size());
        return allUsers.isEmpty() ? null : allUsers;
    }

    @Override
    public User getById(Long id) {
        User result = userRepository.findById(id).orElse(null);
        if(result == null) {
            log.warn("IN getById - no user found by id: {}", id);
            return null;
        }
        log.info("IN getById - user found by id: {}", id);
        return result;
    }

    @Override
    public User getByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN getByUsername - user found by username: {}", username);
        return result;
    }

    @Override
    @Transactional
    public User register(User user) {
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);
        user.setStatus(Status.ACTIVE);
        User registeredUser = userRepository.save(user);

        log.info("IN register - user with id: {} successfully registered", registeredUser.getId());

        return registeredUser;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setStatus(Status.DELETED);
            User deletedUser = userRepository.save(user);
            log.info("IN deleted - user {} found by id: {}", deletedUser, id);
            return true;
        } else {
            log.warn("IN delete - no user found by id: {}", id);
            return false;
        }
    }

    @Override
    @Transactional
    public User update(User user) {
        User userToUpdate = userRepository.findById(user.getId()).orElse(null);
        if (userToUpdate != null) {
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setPassword(passwordEncoder.encode(userToUpdate.getPassword()));

            User savedUser = userRepository.save(userToUpdate);
            log.info("IN update - user with id: {} updated", user.getId());
            return savedUser;
        } else {
            log.warn("IN update - no user found by id: {}", user.getId());
            return null;
        }
    }

    public User getMyInfo(Principal principal) {
        return userRepository.findByUsername(principal.getName());
    }
}
