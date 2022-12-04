package com.vladislav.filestoragerest.service;

import com.vladislav.filestoragerest.model.User;

import java.security.Principal;

public interface UserService extends GenericService<User, Long> {
    User register(User user);
    User update(User user);
    User getByUsername(String username);
    boolean delete (Long id);
    User getMyInfo(Principal principal);
}
